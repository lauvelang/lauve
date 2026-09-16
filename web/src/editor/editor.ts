import {
    canvas,
    canvasMouseX,
    canvasMouseY, closestHookFieldName,
    closestHookNode,
    hoveredField, hoveredFieldRect,
    hoveredNode, hoveredToolboxGroup, isInnerHook, selectedToolboxGroup, selectToolboxGroup,
    setCursor, xInToolbox
} from "../render/render";
import {script} from "./state";
import {Argument, findFirstInStack, findLastInStack, Node} from "../script/node";

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

    if (hoveredToolboxGroup) {
        setCursor("pointer")
    }
}

// Node field data entry
let enteringNode: Node | null = null;
let enteringArg: Argument | null = null;
let fieldInput: HTMLInputElement | null = null;
function endFieldEnter() {
    if (!enteringArg || !fieldInput) return

    enteringArg.value = fieldInput.value;
    enteringArg = null;

    fieldInput.remove();
    fieldInput = null;
}

function startFieldEnter(event: MouseEvent) {
    if (fieldInput) {
        endFieldEnter();
    }

    if (!hoveredNode || !hoveredField || !hoveredFieldRect) return
    enteringNode = hoveredNode;

    fieldInput = document.createElement("input");
    fieldInput.classList.add("popup-input");

    enteringArg = enteringNode.args.get(hoveredField)!;
    fieldInput.value = enteringArg.value;

    fieldInput.style.left = hoveredFieldRect.x + script.camera[0] + "px";
    fieldInput.style.top = hoveredFieldRect.y + script.camera[1] + "px";
    fieldInput.style.width = hoveredFieldRect.width + "px";
    fieldInput.style.height = hoveredFieldRect.height + "px";

    document.body.appendChild(fieldInput);

    fieldInput.addEventListener("blur", () => {
        if (enteringArg) endFieldEnter();
    });
    fieldInput.addEventListener("keyup", e => {
        if (e.key === 'Enter') endFieldEnter();
    });

    fieldInput.focus();
    fieldInput.select();
    event.preventDefault();
}

// Moving nodes
export let isDraggingNode: boolean = false;
export let draggingNode: Node | null = null;
let nodeDragOrigin = [0, 0];

function startNodeDrag(event: MouseEvent) {
    isDraggingNode = true;

    if (hoveredNode && hoveredNode.template) {
        let copy = Node.defaultOf(hoveredNode.opcode, false);
        copy.renderBB.setPos(
            hoveredNode.renderBB.x - script.camera[0],
            hoveredNode.renderBB.y - script.camera[1]
        )
        script.addNode(copy);
        draggingNode = copy;
    } else {
        draggingNode = hoveredNode!;
    }
    draggingNode.markModified();

    nodeDragOrigin = [draggingNode.renderBB.x, draggingNode.renderBB.y];
    dragStart = [event.clientX, event.clientY];
}

function unhookDraggedNode() {
    if (draggingNode == null) return

    let parent = script.lookupNode(draggingNode.parent);
    if (!parent) return;

    if (draggingNode.definition.shape === "input") {
        for (let arg of parent.args.values()) {
            if (arg.resolved || arg.value !== draggingNode.id) continue;

            draggingNode.parent = null;

            arg.resolved = true;
            arg.value = arg.oldResolvedValue;
            break;
        }
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
    if (xInToolbox(canvasMouseX)) {
        draggingNode?.erase()
    } else if (closestHookNode && draggingNode && !draggingNode.parent) {
        if (closestHookFieldName) {
            // Attaching node to inside of other input
            let arg = closestHookNode.args.get(closestHookFieldName);
            if (arg) {
                arg.oldResolvedValue = arg.value;

                arg.resolved = false;
                arg.value = draggingNode.id;
                draggingNode.parent = closestHookNode.id;
            }
        } else {
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
    if (hoveredToolboxGroup) {
        selectToolboxGroup()
        return
    }

    if (!hoveredNode) {
        startCameraMove(event);
    } else {
        if (hoveredField) {
            startFieldEnter(event);
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