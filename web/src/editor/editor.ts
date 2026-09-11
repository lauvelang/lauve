import {hoveredField, hoveredNode, setCursor} from "../render/render";

let draggingNode: Node | null = null;

// Camera drag
let isDraggingCamera: boolean = false;


function tickCursor() {
    if (draggingNode) {
        setCursor("grabbing");
        return
    }

    if (hoveredNode) {
        if (hoveredField) {
            setCursor("text");
        } else {
            setCursor("grab")
        }
    } else {
        setCursor("default")
    }
}

export function tickEditor() {
    tickCursor();
}

export function initEditor(canvas: HTMLCanvasElement) {
    canvas.addEventListener("contextmenu", e => {
        e.preventDefault();
    })
}