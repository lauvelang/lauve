import { flavors } from "@catppuccin/palette";
import {FONT, SMALL_FONT} from "./font";
import {Argument, Node} from "../script/node";
import {script} from "../editor/state";
import {lookupColor, lookupDefinition} from "../script/definitions";
import {lookupForNode} from "../editor/translations";
let flavor = flavors.mocha.colors;

const canvas = document.querySelector("canvas")!;
const ctx = canvas.getContext("2d")!;

if (!ctx || !canvas) throw new Error("Unable to create canvas context");

// DEBUG CODE REMOVE

let __count = 0
const __detectInfiniteLoop = () => {
    if (__count > 10000) {
        throw new Error('Infinite Loop detected')
    }
    __count += 1
}

// DEBUG CODE REMOVE

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

// Hold mouse state for immediate mode stuff
let canvasMouseX = 0;
let canvasMouseY = 0;

// Rendering

/*
hi ~~tomorrow me!!!!~~ whoever is reading this
okay so basically how we are gonna do this is by
- taking a "mother of all nodes" node and a pos
- recurse through children and get their sizes
- render with those sizes

also, i learned that (x / 2) - (y / 2) = (x - y) / 2; ive been doing it wrong my entire life bruh
 */

const NODE_PADDING_WIDTH = 10;
const NODE_PADDING_HEIGHT = 8;

const doublePaddingW = NODE_PADDING_WIDTH * 2;
const doublePaddingH = NODE_PADDING_HEIGHT * 2;

const PART_MARGIN = 8;

const FIELD_MIN_WIDTH = 64;
const FIELD_HEIGHT = 24;
const FIELD_PADDING = 12;

const CHILD_HAVER_INDENT = 8;
const CHILD_HAVER_SPACE = 24;
const CHILD_HAVER_BASE = 24;

function getStackDimensions(root: Node): [number, number] {
    let width = 0
    let height = 0;
    let current: Node | undefined = root;
    while (current) {
        calculateSizes(current);
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

    let definition = lookupDefinition(root.opcode);
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

        /*
        let child = root.getFirstChild(script);
        if (child) {
            let [cWidth, cHeight] = getStackDimensions(child);
            width = Math.max(width, cWidth)
            height += cHeight;
        } else {
            height += CHILD_HAVER_SPACE;
        }*/
    }

    root.renderInfo.setSize(width, height);
}

function renderFullNode(root: Node) {
    let cx = root.renderInfo.x + NODE_PADDING_WIDTH;
    let y = root.renderInfo.y;

    let color = lookupColor(root.opcode.namespace)
    fillColor(color);
    ctx.fillRect(root.renderInfo.x, root.renderInfo.y, root.renderInfo.width, root.renderInfo.height);

    let definition = lookupDefinition(root.opcode);
    for (let part of definition.description) {
        switch (part.type) {
            case "label": {
                // TODO: Cache text width?
                let content = lookupForNode(root.opcode, part.id);
                let contentMetrics = ctx.measureText(content);

                fillColor(flavor.crust.hex);
                fontAlignment("left", "middle")
                ctx.fillText(content, cx, y + root.renderInfo.height / 2);
                cx += contentMetrics.width;
                break;
            }
            case "input": {
                let arg = root.args.get(part.id)!;
                if (arg.resolved) {
                    let fWidth = calculateFieldWidth(arg);

                    let fy = y + ((root.renderInfo.height - FIELD_HEIGHT) / 2);
                    fillColor(flavor.text.hex);
                    ctx.fillRect(cx, fy, fWidth, FIELD_HEIGHT);

                    fillColor(flavor.crust.hex);
                    fontAlignment("center", "middle")
                    ctx.fillText(arg.value, cx + (fWidth / 2), fy + (FIELD_HEIGHT / 2))

                    cx += fWidth;
                } else {
                    let node = script.lookupNode(arg.value)!;
                    node.renderInfo.x = cx;
                    node.renderInfo.y = y + ((root.renderInfo.height - node.renderInfo.height) / 2);
                    renderFullNode(node);
                    cx += node.renderInfo.width;
                }

                break;
            }
        }
        cx += PART_MARGIN;
    }

    /*if (root.mayHaveChild()) {
        let nodeHeight = root.renderInfo.height;
        fillColor(color);
        ctx.fillRect(root.renderInfo.x, y, CHILD_HAVER_INDENT, nodeHeight);
        ctx.fillRect(root.renderInfo.x, y + nodeHeight - CHILD_HAVER_BASE, root.renderInfo.width, nodeHeight)
    }*/
}

function draw() {
    let frameStart = performance.now();

    // Reset various things
    resetFont();
    fontAlignment();
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.save();
    ctx.translate(script.camera[0], script.camera[1]);

    // Render stuff
    for (let node of script.nodes.values()) {
        if (node.parent) continue;

        let x = node.renderInfo.x;
        let y = node.renderInfo.y;
        let current: Node | undefined = node;
        while (current != null) {
            current.renderInfo.x = x;
            current.renderInfo.y = y;

            calculateSizes(current);
            renderFullNode(current);

            y += current.renderInfo.height;

            current = script.lookupNode(current.next);
        }

    }

    ctx.restore();

    // Debugging information
    let frameDelta = performance.now() - frameStart;
    fontAlignment("right", "top");

    const debugLines: [string, string][] = [
        [flavor.text.hex, `${frameDelta.toFixed(1)}ms`],
        [flavor.red.hex, `${script.name}`],
        [flavor.peach.hex, "made with <3 by sylvie"]
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
    });

    // Start draw loop
    requestAnimationFrame(draw);
}