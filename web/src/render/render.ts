import { flavors } from "@catppuccin/palette";
import {FONT, SMALL_FONT} from "./font";
import {Argument, Node} from "../script/node";
import {script} from "../editor/state";
import {lookupColor, NodeShapeConnectivity} from "../script/definitions";
import {lookupForNode} from "../editor/translations";
import {pointInBounds, Rectangle} from "../utils";
import {draggingNode, initEditor, isDraggingNode, tickEditor} from "../editor/editor";
import {hslToRgb, intArrayToString, rgbToHsl} from "./color";
let flavor = flavors.mocha.colors;

export const canvas = document.querySelector("canvas")!;
const ctx = canvas.getContext("2d")!;

if (!ctx || !canvas) throw new Error("Unable to create canvas context");

function initCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
}

// Utility functions
function fillColor(color: string) {
    ctx.fillStyle = color;
}

function strokeColor(color: string) {
    ctx.strokeStyle = color;
}

function resetFont() {
    ctx.font = FONT;
}

function smallFont() {
    ctx.font = SMALL_FONT;
}

function fontAlignment(align: CanvasTextAlign = "left", baseline: CanvasTextBaseline = "top") {
    ctx.textAlign = align;
    ctx.textBaseline = baseline;
}

function textHeight(metrics: TextMetrics) {
    return metrics.actualBoundingBoxAscent + metrics.actualBoundingBoxDescent;
}

const NOTCH_WIDTH = 20;
const NOTCH_DEPTH = 4;
function notchedRect(x: number, y: number, width: number, height: number, topX: number, bottomX: number, fill: boolean = true) {
    function indent(inX: number, inY: number, reverse: boolean = false) {
        let notchStart = x + inX;
        let notchEnd = notchStart + NOTCH_WIDTH;

        if (reverse) { [notchStart, notchEnd] = [notchEnd, notchStart] }

        let notchY = inY + NOTCH_DEPTH;
        ctx.lineTo(notchStart, inY)
        ctx.lineTo(notchStart, notchY);
        ctx.lineTo(notchEnd, notchY);
        ctx.lineTo(notchEnd, inY);
    }

    let endX = x + width;
    let endY = y + height;
    ctx.beginPath();

    ctx.moveTo(x, y);
    if (topX != 0) {
        indent(topX, y)
    }

    ctx.lineTo(endX, y);
    ctx.lineTo(endX, endY);

    if (bottomX != 0) {
        indent(bottomX, endY, true)
    }

    ctx.lineTo(x, endY);
    ctx.lineTo(x, y)

    if (fill)
        ctx.fill();
    else
        ctx.stroke()
}

function notchedRectOutlined(x: number, y: number, width: number, height: number, topX: number, bottomX: number) {
    notchedRect(x, y, width, height, topX, bottomX, true);
    notchedRect(x, y, width, height, topX, bottomX, false);
}

function strokeAndFillRect(x: number, y: number, width: number, height: number) {
    ctx.fillRect(x, y, width, height);
    ctx.strokeRect(x, y, width, height);
}

function strokeAndFillText(text: string, x: number, y: number, strokeColor: string, fillColor: string) {
    ctx.strokeStyle = strokeColor;
    ctx.strokeText(text, x, y);

    ctx.fillStyle = fillColor;
    ctx.fillText(text, x, y);
}

export function setCursor(cursor: string) {
    canvas.style.cursor = cursor;
}

// For culling
function isOnScreen(rect: Rectangle) {
    let x = rect.x + script.camera[0];
    let y = rect.y + script.camera[1];

    let x2 = x + rect.width;
    let y2 = y + rect.height;

    // b is screen
    return x2 >= 0 && x <= canvas.width && y <= canvas.height && y2 >= 0
}

// Hold mouse state for immediate mode stuff
export let canvasMouseX = 0;
export let canvasMouseY = 0;

let workMouseX = 0;
let workMouseY = 0;

// Editor logic stuff
export let hoveredNode: Node | null = null;
export let hoveredFieldRect: Rectangle | null = null;
export let hoveredField: string | null = null;

const MAX_HOOK_DISTANCE = 32;
let closestHookPointDist = Infinity;
let closestHookPointPos = [0, 0];
export let closestHookNode: Node | null = null;
export let closestHookFieldName: string | null = null;
export let isInnerHook = false;

function compareHookPoint(x: number, y: number, node: Node, inner: boolean, fieldName: string | null = null) {
    if (!draggingNode || node === draggingNode) return;

    let isDraggedInput = draggingNode.definition.shape === "input";
    if (!isDraggedInput && fieldName) return;

    let [nx, ny] = isDraggedInput ?
        [workMouseX, workMouseY] :
        [draggingNode.renderBB.x, draggingNode.renderBB.y];

    let distance = Math.hypot(
        nx - x,
        ny - y
    )
    if (distance > MAX_HOOK_DISTANCE || distance > closestHookPointDist) return;

    closestHookPointPos = [x, y];
    closestHookPointDist = distance;
    closestHookNode = node;
    closestHookFieldName = fieldName;
    isInnerHook = inner;
}

// Rendering
const NODE_PADDING_WIDTH = 10;
const NODE_PADDING_HEIGHT = 8;

const doublePaddingW = NODE_PADDING_WIDTH * 2;
const doublePaddingH = NODE_PADDING_HEIGHT * 2;

const PART_MARGIN = 8;

const FIELD_MIN_WIDTH = 64;
const FIELD_HEIGHT = 24;
const FIELD_PADDING = 12;

const CHILD_HAVER_INDENT = 16;
const CHILD_HAVER_SPACE = 24;
const CHILD_HAVER_BASE = 24;
const CHILD_HAVER_OVERHANG = 32;

function getStackDimensions(root: Node, recalculate: boolean = true): [number, number] {
    let width = 0
    let height = 0;
    let current: Node | undefined = root;
    while (current) {
        if (recalculate) calculateSizes(current);
        width = Math.max(width, current.renderBB.width);
        height += current.renderBB.height;

        current = script.lookupNode(current.next);
    }

    return [width, height];
}

function calculateFieldWidth(arg: Argument): number {
    let text = arg.value ?? "";
    let contentMetrics = ctx.measureText(text);

    return Math.max(contentMetrics.width, FIELD_MIN_WIDTH) + FIELD_PADDING;
}

function calculateSizes(root: Node) {
    let width = doublePaddingW;
    let height = 0;

    let definition = root.definition;
    for (let part of definition.description) {
        switch (part.type) {
            case "label": {
                resetFont();
                let content = lookupForNode(root.opcode, part.id);
                let contentMetrics = ctx.measureText(content);

                height = Math.max(height, textHeight(contentMetrics) + doublePaddingH);
                width += contentMetrics.width;
                break
            }

            case "input": {
                let arg = root.args.get(part.id)!;

                if (arg.resolved) {
                    let fWidth = calculateFieldWidth(arg);
                    width += fWidth;
                    height = Math.max(height, FIELD_HEIGHT + doublePaddingH);
                } else {
                    let node = script.lookupNode(arg.value)!;

                    calculateSizes(node);

                    width += node.renderBB.width;
                    height = Math.max(height, node.renderBB.height + doublePaddingH);
                }
            }
        }
        width += PART_MARGIN;
    }
    width -= PART_MARGIN;

    // Nodes with inner nodes
    if (root.mayHaveChild()) {
        height += CHILD_HAVER_BASE;

        let child = root.getFirstChild(script);
        if (child) {
            let [cWidth, cHeight] = getStackDimensions(child);
            width = Math.max(width, cWidth + CHILD_HAVER_INDENT + CHILD_HAVER_OVERHANG)
            height += cHeight;
        } else {
            height += CHILD_HAVER_SPACE;
        }
    }

    root.renderBB.setSize(width, height);
}

const NOTCH_OFFSET = 12;
function renderFullNode(root: Node) {
    let definition = root.definition;
    let isDraggedInput = draggingNode?.definition.shape === "input";

    let cx = root.renderBB.x + NODE_PADDING_WIDTH;
    let y = root.renderBB.y;

    let color = lookupColor(root.opcode.namespace)

    let blockWidth = root.renderBB.width;
    let blockHeight = root.renderBB.height;
    let child = null;
    let tailHeight = CHILD_HAVER_SPACE;

    // Handle any immediate state
    if (root.renderBB.isInside(workMouseX, workMouseY)) {
        hoveredNode = root;
        hoveredField = null;
    }

    if (isDraggingNode && !isDraggedInput) {
        compareHookPoint(root.renderBB.x, root.renderBB.y + blockHeight, root, false)
    }

    // Handle child nodes
    if (root.mayHaveChild()) {
        blockHeight -= CHILD_HAVER_BASE;

        let innerX = root.renderBB.x + CHILD_HAVER_INDENT;
        child = root.getFirstChild(script);
        if (child) {
            let [_, height] = getStackDimensions(child, false);
            blockHeight -= height;
            tailHeight = height;
            drawNodeStack(child, innerX, root.renderBB.y + blockHeight);
        } else {
            blockHeight -= CHILD_HAVER_SPACE;
        }

        if (!isDraggedInput) compareHookPoint(innerX, root.renderBB.y + blockHeight, root, true)
    }

    // Render self
    fillColor(intArrayToString(color));

    let outline = rgbToHsl(...color);
    outline[2] *= 0.9;
    outline = hslToRgb(...outline)
    strokeColor(intArrayToString(outline));

    if (definition.shape !== "input") {
        let connectivity  = NodeShapeConnectivity[definition.shape];
        let topNotch = connectivity.top ? NOTCH_OFFSET : 0;
        let bottomNotch = connectivity.bottom ? NOTCH_OFFSET + (root.mayHaveChild() ? CHILD_HAVER_INDENT : 0) : 0;
        notchedRectOutlined(
            root.renderBB.x, root.renderBB.y, blockWidth, blockHeight, topNotch, bottomNotch);

        if (root.mayHaveChild()) { // draw the tail
            let bottomY = root.renderBB.y + blockHeight;

            notchedRectOutlined(root.renderBB.x, bottomY + tailHeight, blockWidth, CHILD_HAVER_BASE,
                NOTCH_OFFSET + CHILD_HAVER_INDENT,
                connectivity.bottom ? NOTCH_OFFSET : 0)

            ctx.strokeRect(root.renderBB.x, bottomY, CHILD_HAVER_INDENT, tailHeight);
            ctx.fillRect(root.renderBB.x + (ctx.lineWidth / 2), bottomY - ctx.lineWidth, CHILD_HAVER_INDENT - ctx.lineWidth, tailHeight + (ctx.lineWidth * 2));
        }
    } else {
        strokeAndFillRect(root.renderBB.x, root.renderBB.y, blockWidth, blockHeight);
    }

    for (let part of definition.description) {
        switch (part.type) {
            case "label": {
                // TODO: Cache text width?
                let content = lookupForNode(root.opcode, part.id);
                let contentMetrics = ctx.measureText(content);

                fillColor(flavor.crust.hex);
                fontAlignment("left", "middle")
                ctx.fillText(content, cx, y + blockHeight / 2);
                cx += contentMetrics.width;
                break;
            }
            case "input": {
                let arg = root.args.get(part.id)!;
                if (arg.resolved) {
                    let fWidth = calculateFieldWidth(arg);

                    let fy = y + ((blockHeight - FIELD_HEIGHT) / 2);
                    fillColor(flavor.text.hex);
                    strokeColor(flavor.subtext0.hex);

                    strokeAndFillRect(cx, fy, fWidth, FIELD_HEIGHT);

                    fillColor(flavor.crust.hex);
                    fontAlignment("center", "middle")
                    let tx = cx + (fWidth / 2);
                    let ty = fy + (FIELD_HEIGHT / 2);
                    ctx.fillText(arg.value !== null ? arg.value : "", tx, ty)

                    let fieldRect = new Rectangle(cx, fy, fWidth, FIELD_HEIGHT);
                    if (fieldRect.isInside(workMouseX, workMouseY)) {
                        hoveredField = part.id;
                        hoveredFieldRect = fieldRect;
                    }

                    if (!draggingNode?.isPartOfSelf(root)) compareHookPoint(tx, ty, root, false, part.id);

                    cx += fWidth;
                } else {
                    let node = script.lookupNode(arg.value)!;
                    node.renderBB.x = cx;
                    node.renderBB.y = y + ((blockHeight - node.renderBB.height) / 2);
                    renderFullNode(node);
                    cx += node.renderBB.width;
                }

                break;
            }
        }
        cx += PART_MARGIN;
    }
}

// Returns height of stack
function drawNodeStack(root: Node, x: number, y: number): number {
    let cy = y;
    let current: Node | undefined = root;
    while (current != null) {
        current.renderBB.x = x;
        current.renderBB.y = cy;

        calculateSizes(current);
        if (isOnScreen(current.renderBB)) renderFullNode(current);

        cy += current.renderBB.height;

        current = script.lookupNode(current.next);
    }

    return cy - y;
}

function drawHookIndicator() {
    if (!closestHookNode) return

    fontAlignment("right", "middle")

    let [x, y] = closestHookPointPos;
    strokeAndFillText("→",
        x - 4,
        y,
        flavor.crust.hex,
        flavor.text.hex);
}

const GRID_SIZE = 32;
function updateBackground() {
    let xOffset = script.camera[0] % GRID_SIZE;
    let yOffset = script.camera[1] % GRID_SIZE;
    document.body.style.backgroundPosition = `${xOffset}px ${yOffset}px`;
}

function draw(delta: number = 0) {
    let frameStart = performance.now();

    // Reset rendering
    resetFont();
    fontAlignment();
    ctx.lineWidth = 2;
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.save();
    ctx.translate(script.camera[0], script.camera[1]);

    // Reset immediate state
    hoveredField = null;
    hoveredNode = null;

    closestHookPointDist = Infinity
    closestHookNode = null
    closestHookFieldName = null;
    isInnerHook = false

    // Render stuff
    let renderables = Array.from(script.nodes.values())
        .filter(n => !n.parent)
        .sort((a, b) => a.lastModified - b.lastModified);
    for (let node of renderables) {
        drawNodeStack(node, node.renderBB.x, node.renderBB.y);
    }

    drawHookIndicator();

    // Handle dragging, clicking, etc.
    tickEditor()

    ctx.restore();

    // Debugging information
    let frameDelta = performance.now() - frameStart;
    fontAlignment("right", "top");

    const debugLines: [string, string][] = [
        [flavor.text.hex, `${frameDelta.toFixed(2)}ms`],
        [flavor.red.hex, `${script.name}`],
        [flavor.yellow.hex, "N: " + (hoveredNode != null ? (<Node> hoveredNode).id : "[no hover]")],
        [flavor.yellow.hex, "F: " + (hoveredField != null ? hoveredField : "[no field]")],
        [flavor.green.hex, `R: ${canvasMouseX}, ${canvasMouseY}`],
        [flavor.green.hex, `C: ${workMouseX}, ${workMouseY}`],
        [flavor.blue.hex, `${closestHookNode} (${closestHookPointDist}px)`]
    ]

    for (let i = 0; i < debugLines.length; i++) {
        let [color, line] = debugLines[i];
        fillColor(color);
        ctx.fillText(line, canvas.width - 4, 4 + (i * 18))
    }

    updateBackground()

    // Queue next frame
    requestAnimationFrame(draw);
}

export function initRenderer() {
    // Scale the canvas to the entire size of the window
    initCanvas();
    window.addEventListener("resize", () => initCanvas());

    // Keep track of mouse state
    canvas.addEventListener("mousemove", (event) => {
        canvasMouseX = event.clientX;
        canvasMouseY = event.clientY;

        workMouseX = canvasMouseX - script.camera[0]
        workMouseY = canvasMouseY - script.camera[1]
    });

    // Add events to canvas
    initEditor(canvas);

    // Start draw loop
    requestAnimationFrame(draw);
}