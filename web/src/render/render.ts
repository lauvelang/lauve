import { flavors } from "@catppuccin/palette";
import {FONT, SMALL_FONT} from "./font";
import {Argument, Node} from "../script/node";
import {script} from "../editor/state";
import {lookupColor, NodeShapeConnectivity} from "../script/definitions";
import {lookupForNode} from "../editor/translations";
import {pointInBounds, Rectangle} from "../utils";
import {initEditor, tickEditor} from "../editor/editor";
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
function notchedFillRect(x: number, y: number, width: number, height: number, topX: number, bottomX: number) {
    function indent(inX: number, inY: number) {
        let notchStart = x + inX;
        let notchEnd = notchStart + NOTCH_WIDTH;

        if (notchStart > notchEnd) {
            [notchEnd, notchStart] = [notchStart, notchEnd];
        }

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
        indent(bottomX, endY)
    }

    ctx.lineTo(x, endY);
    ctx.lineTo(x, y)

    ctx.strokeStyle = ctx.fillStyle
    ctx.fill();
}

export function setCursor(cursor: string) {
    canvas.style.cursor = cursor;
}

// For culling
let screenRect = new Rectangle(0, 0, canvas.width, canvas.height);

// Hold mouse state for immediate mode stuff
let canvasMouseX = 0;
let canvasMouseY = 0;

let workMouseX = 0;
let workMouseY = 0;

// Editor logic stuff
export let hoveredNode: Node | null = null;
export let hoveredField: string | null = null;

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
        width = Math.max(width, current.renderInfo.width);
        height += current.renderInfo.height;

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

                    width += node.renderInfo.width;
                    height = Math.max(height, node.renderInfo.height + doublePaddingH);
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

    root.renderInfo.setSize(width, height);
}

const NOTCH_OFFSET = 12;
function renderFullNode(root: Node) {
    let definition = root.definition;

    let cx = root.renderInfo.x + NODE_PADDING_WIDTH;
    let y = root.renderInfo.y;

    let color = lookupColor(root.opcode.namespace)

    let blockWidth = root.renderInfo.width;
    let blockHeight = root.renderInfo.height;
    let child = null;
    let tailHeight = CHILD_HAVER_SPACE;
    if (root.mayHaveChild()) {
        blockHeight -= CHILD_HAVER_BASE;

        child = root.getFirstChild(script);
        if (child) {
            let [_, height] = getStackDimensions(child, false);
            blockHeight -= height;
            tailHeight = height;
            drawNodeStack(child, root.renderInfo.x + CHILD_HAVER_INDENT, root.renderInfo.y + blockHeight);
        } else {
            blockHeight -= CHILD_HAVER_SPACE;
        }
    }

    fillColor(color);

    if (definition.shape !== "input") {
        let connectivity  = NodeShapeConnectivity[definition.shape];
        notchedFillRect(
            root.renderInfo.x, root.renderInfo.y, blockWidth, blockHeight,
            connectivity.top ? NOTCH_OFFSET : 0,
            connectivity.bottom ? NOTCH_OFFSET + (root.mayHaveChild() ? CHILD_HAVER_INDENT : 0) : 0);

        if (child) { // draw the tail
            let bottomY = root.renderInfo.y + blockHeight;
            ctx.fillRect(root.renderInfo.x, bottomY, CHILD_HAVER_INDENT, tailHeight);
            notchedFillRect(root.renderInfo.x, bottomY + tailHeight, blockWidth, CHILD_HAVER_BASE,
                NOTCH_OFFSET + CHILD_HAVER_INDENT,
                connectivity.bottom ? NOTCH_OFFSET : 0)
        }
    } else {
        ctx.fillRect(root.renderInfo.x, root.renderInfo.y, blockWidth, blockHeight)
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

                    ctx.fillRect(cx, fy, fWidth, FIELD_HEIGHT);

                    fillColor(flavor.crust.hex);
                    fontAlignment("center", "middle")
                    ctx.fillText(arg.value, cx + (fWidth / 2), fy + (FIELD_HEIGHT / 2))

                    if (pointInBounds(cx, fy, fWidth, FIELD_HEIGHT, workMouseX, workMouseY)) {
                        hoveredField = part.id;
                    }

                    cx += fWidth;
                } else {
                    let node = script.lookupNode(arg.value)!;
                    node.renderInfo.x = cx;
                    node.renderInfo.y = y + ((blockHeight - node.renderInfo.height) / 2);
                    renderFullNode(node);
                    cx += node.renderInfo.width;
                }

                break;
            }
        }
        cx += PART_MARGIN;
    }

    if (root.renderInfo.isInside(workMouseX, workMouseY)) {
        hoveredNode = root;
    }
}

// Returns height of stack
function drawNodeStack(root: Node, x: number, y: number): number {
    let cy = y;
    let current: Node | undefined = root;
    while (current != null) {
        current.renderInfo.x = x;
        current.renderInfo.y = cy;

        calculateSizes(current);
        renderFullNode(current);

        cy += current.renderInfo.height;

        current = script.lookupNode(current.next);
    }

    return cy - y;
}

function draw() {
    let frameStart = performance.now();

    // Reset various things
    resetFont();
    fontAlignment();
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.save();
    ctx.translate(script.camera[0], script.camera[1]);

    screenRect.setPos(script.camera[0], script.camera[1]);
    screenRect.setSize(canvas.width, canvas.height);

    hoveredField = null;
    hoveredNode = null;

    // Render stuff
    for (let node of script.nodes.values()) {
        if (node.parent) continue;

        drawNodeStack(node, node.renderInfo.x, node.renderInfo.y);
    }

    // Handle dragging, clicking, etc.
    tickEditor()

    ctx.restore();

    // Debugging information
    let frameDelta = performance.now() - frameStart;
    fontAlignment("right", "top");

    const debugLines: [string, string][] = [
        [flavor.text.hex, `${frameDelta.toFixed(1)}ms`],
        [flavor.red.hex, `${script.name}`],
        [flavor.yellow.hex, "N: " + (hoveredNode != null ? (<Node> hoveredNode).id : "[no hover]")],
        [flavor.yellow.hex, "F: " + (hoveredField != null ? hoveredField : "[no field]")],
        [flavor.green.hex, `R: ${canvasMouseX}, ${canvasMouseY}`],
        [flavor.green.hex, `C: ${workMouseX}, ${workMouseY}`],
    ]

    for (let i = 0; i < debugLines.length; i++) {
        let [color, line] = debugLines[i];
        fillColor(color);
        ctx.fillText(line, canvas.width - 4, 4 + (i * 18))
    }

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