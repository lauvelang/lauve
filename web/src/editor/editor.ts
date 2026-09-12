import {
    canvas,
    canvasMouseX,
    canvasMouseY,
    closestHookNode,
    hoveredField,
    hoveredNode, isInnerHook,
    setCursor
} from "../render/render";
import {script} from "./state";
import {findFirstInStack, findLastInStack, Node} from "../script/node";

// You can't drag the camera and a node at the same time, so these are shared
let dragStart: [number, number] = [0, 0];  // Where the mouse was when starting a move
let dragOrigin: [number, number] = [0, 0]; // Where the camera started at

function calcDragDistance(): [number, number] {
    return [canvasMouseX - dragStart[0], canvasMouseY - dragStart[1]];
}

// Camera moving
let isDraggingCamera: boolean = false; // If we are actively moving camera

function startCameraMove(event: MouseEvent) {
    isDraggingCamera = true;
    dragOrigin = [script.camera[0], script.camera[1]]
    dragStart = [event.clientX, event.clientY];
}

function tickCameraMove() {
    let [dx, dy] = calcDragDistance();
    script.setCamera(dragOrigin[0] + dx, (dragOrigin[1] + dy));
}

// Modify the cursor depending on editor context
function tickCursor() {
    if (isDraggingNode) {
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

// Node field data entry
function handleFieldEnter() {

}

// Moving nodes
export let isDraggingNode: boolean = false;
export let draggingNode: Node | null = null;
let nodeDragOrigin = [0, 0];

function startNodeDrag(event: MouseEvent) {
    isDraggingNode = true;
    draggingNode = hoveredNode!;

    draggingNode.markModified();

    nodeDragOrigin = [draggingNode.renderBB.x, draggingNode.renderBB.y];
    dragStart = [event.clientX, event.clientY];
}

function unhookDraggedNode() {
    if (draggingNode == null) return

    let parent = script.lookupNode(draggingNode.parent);
    if (!parent) return;

    if (draggingNode.definition.shape === "input") {

    } else {
        if (parent.getFirstChild(script) === draggingNode) {
            parent.setFirstChild(null)
        } else {
            parent.next = null;
        }

        draggingNode.parent = null;
    }
}

function tickNodeDrag() {
    if (draggingNode == null) return

    let [dx, dy] = calcDragDistance();
    if (draggingNode.parent) {
        let dist = Math.hypot(dx, dy);
        if (dist > 10)
            unhookDraggedNode();
        return;
    }

    let newX = nodeDragOrigin[0] + dx;
    let newY = nodeDragOrigin[1] + dy;
    draggingNode.renderBB.setPos(newX, newY);
}

function finishNodeDrag() {
    if (closestHookNode && draggingNode) {
        let last = findLastInStack(draggingNode);
        if (isInnerHook) {
            let previousChild = closestHookNode.getFirstChild(script);
            if (previousChild)
                last.linkNext(previousChild);


            draggingNode.parent = closestHookNode.id;
            closestHookNode.setFirstChild(draggingNode);
        } else {
            let previousNext = script.lookupNode(closestHookNode.next);
            if (previousNext)
                last.linkNext(previousNext);

            closestHookNode.linkNext(draggingNode);
        }

        let first = findFirstInStack(draggingNode);
        if (first) first.markModified();
    }

    draggingNode = null;
    isDraggingNode = false;
}

export function tickEditor() {
    tickCursor();

    if (isDraggingCamera) {
        tickCameraMove()
    } else if (isDraggingNode) {
        tickNodeDrag()
    }
}

function handleMouseDown(event: MouseEvent) {
    if (!hoveredNode) {
        startCameraMove(event);
    } else {
        if (hoveredField) {
            handleFieldEnter();
        } else {
            startNodeDrag(event);
        }
    }
}

function handleMouseUp(event: MouseEvent) {
    isDraggingCamera = false;
    if (isDraggingNode)
        finishNodeDrag()

}

export function initEditor(canvas: HTMLCanvasElement) {
    canvas.addEventListener("contextmenu", e => {
        e.preventDefault();
    })

    canvas.addEventListener("mousedown", handleMouseDown);
    canvas.addEventListener("mouseup", handleMouseUp);
}