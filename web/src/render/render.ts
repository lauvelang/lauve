import { flavors } from "@catppuccin/palette";
import {FONT, SMALL_FONT} from "./font";
import {Node} from "../script/node";
let flavor = flavors.mocha.colors;

const canvas = document.querySelector("canvas")!;
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

// Rendering

/*
hi ~~tomorrow me!!!!~~ whoever is reading this
okay so basically how we are gonna do this is by
- taking a "mother of all nodes" node and a pos
- recurse through children and get their sizes
- render with those sizes

also, i learned that (x / 2) - (y / 2) = (x - y) / 2; ive been doing it wrong my entire life bruh
 */

function layoutNode(node: Node, x: number, y: number) {

}

function draw() {
    let frameStart = performance.now();

    // Reset various things
    resetFont();
    fontAlignment();
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Render stuff

    
    // Debugging information
    let frameDelta = performance.now() - frameStart;
    fontAlignment("right");

    const debugLines: [string, string][] = [
        [flavor.text.hex, `${frameDelta.toFixed(1)}ms`],
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
    initCanvas();
    window.addEventListener("resize", () => initCanvas());

    draw();
}