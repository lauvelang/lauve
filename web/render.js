/*
 * im so sorry for the code you are about to read
 * this is evil bodged javascript
 */
import {Id, SCRIPT, lookupNode, lookupDefinition} from "./state.js";

const canvas = document.getElementById("workspace-canvas");
const ctx = canvas.getContext("2d");

function translate(id, name) {
    let translations = window.translations?.[id.namespace]?.[id.path];
    return translations?.[name] ?? name;
}

// Converts an RGB int array to a CSS string
function toCss(color) {
    return `rgb(${color[0]}, ${color[1]}, ${color[2]})`
}

const SHADING = 2;
// Draws a rectangle with "highlights" and "shadows"; height includes shading!
function drawShadedRect(x, y, width, height, color, top = true, bottom = true, horizontalShading = false) {
    // TODO: color theorists would execute me if they saw this
    function lighten(v) {
        return Math.min(v * 1.2, 255);
    }

    function darken(v) {
        return Math.max(v / 1.2, 0);
    }

    let [r, g, b] = color;

    ctx.fillStyle = toCss(color);
    ctx.fillRect(x, y, width, height);

    if (top) {
        ctx.fillStyle = toCss([lighten(r), lighten(g), lighten(b)]);
        ctx.fillRect(x, y, width, SHADING);

        if (horizontalShading)
            ctx.fillRect(x, y, SHADING, height - SHADING);

    }

    if (bottom) {
        ctx.fillStyle = toCss([darken(r), darken(g), darken(b)]);
        ctx.fillRect(x, y + height - SHADING, width, SHADING);

        if (horizontalShading)
            ctx.fillRect(x + width, y, SHADING, height);
    }
}

const TEXT = [30, 30, 46];
// Draws text
function drawText(text, x, y, color, baseline, alignment) {
    ctx.fillStyle = toCss(color);
    ctx.textBaseline = baseline ?? "top";
    ctx.textAlign = alignment ?? "left";
    ctx.fillText(text, x, y);
}

// How each node may connect with others
const SHAPES = { // first = top availability, second = bottom availability
    "start": [false, true],
    "normal": [true, true],
    "end": [true, false],
    "input": [false, false]
}

// Style variables for how blocks should be rendered
const NODE_XPAD = 8; // Padding of block in each horizontal direction
const NODE_YPAD = 6; // Padding of block in each vertical direction
const CONNECTOR_START = 16; // How far into the block the connector bit starts
const CONNECTOR_WIDTH = 20; // How wide the connector reaches
const CONNECTOR_HEIGHT = 4; // How much the connector moves vertically
const PART_MARGIN = 8; // How far each part of the block is separated
const INDENT = 12; // How far each nest goes inward
const END_PART = 24; // The blank end of child having blocks
const MINIMUM_INPUT = 36;

// Draw a rectangle with connector
function drawConnectedRect(x, y, width, height, color, top, bottom, topOffset, bottomOffset, horizontalShading = false) {
    if (top) {
        let start = CONNECTOR_START + topOffset;
        let postConnector = start + CONNECTOR_WIDTH
        drawShadedRect(x, y, start, height, color); // before connector
        drawShadedRect(x + start, y + CONNECTOR_HEIGHT, CONNECTOR_WIDTH, height - CONNECTOR_HEIGHT, color) // connector
        drawShadedRect(x + postConnector, y, width - postConnector, height, color) // after connector
    } else {
        drawShadedRect(x, y, width, height, color, true, true, horizontalShading)
    }

    if (bottom) {
        let start = CONNECTOR_START + bottomOffset;
        drawShadedRect(x + start, y + height - SHADING, CONNECTOR_WIDTH, CONNECTOR_HEIGHT + SHADING, color, false, true)
    }
}

// Hoverable nodes
let renderCount = 0;
let hitboxes = {};

// Draw a node in the script format
function drawNode(rid, node, x, y, immediatelyRender = true, z = 0) {
    let opcode = Id.fromString(node.opcode);
    let group = window.blocks[opcode.namespace];
    let definition = group[opcode.path];
    let description = definition.description;

    let baseColor = group.color;

    let nodeShape = definition.shape;
    let isInput = nodeShape === "input"
    let [mayConnectTop, mayConnectBottom] = SHAPES[nodeShape];

    let mayHaveChildren = definition.has_children;
    let deferred = []; // We need to wait before rendering a lot of the node
    let overlays = [];

    let yPad = isInput ? 4 : NODE_YPAD
    let doubleYPad = yPad * 2;

    let height = 0;
    let xPad = NODE_XPAD + (isInput ? 4 : 0)

    let hitbox = {"self": [], "fields": {}, "rc": renderCount++};
    let endX = x + xPad;
    for (let component of description) {
        let thisX = endX;
        switch (component.type) {
            case "label": {
                const translated = translate(opcode, component.id)
                const textMetrics = ctx.measureText(translated);
                endX += textMetrics.width;

                deferred.push(() => {
                    drawText(translated, thisX, y + (height / 2), TEXT, "middle");
                })

                let textHeight = textMetrics.actualBoundingBoxAscent + textMetrics.actualBoundingBoxDescent;
                height = Math.max(height, textHeight);

                break;
            }
            case "input": {
                let padding = 4;

                let thisWidth = 0;
                let thisHeight = 24;

                let [resolved, value] = node["args"][component.id];
                if (resolved) {
                    const textMetrics = ctx.measureText(value);
                    thisWidth = Math.max(MINIMUM_INPUT, textMetrics.width + (padding * 2));

                    deferred.push(() => {
                        let thisY = y + (height / 2) - (thisHeight / 2)
                        drawShadedRect(thisX, thisY, thisWidth, thisHeight, [205, 214, 244], true, true, isInput)
                        drawText(value, thisX + (thisWidth / 2), y + (height / 2), TEXT, "middle", "center");

                        hitbox.fields[component.id] = [thisX, thisY, thisWidth, thisHeight]
                    })

                    height = Math.max(height, thisHeight + (padding));
                } else {
                    let node = lookupNode(value);
                    let [aWidth, aHeight, commands, aOverlays] = drawNode(value, node, thisX, y, false, z + 1);
                    thisWidth += aWidth;
                    height = Math.max(height, aHeight);
                    overlays.push([value, height, commands]);
                    overlays.push(...aOverlays);
                }

                endX += thisWidth;
            }
        }
        endX += PART_MARGIN;
    }
    endX -= PART_MARGIN;
    height += doubleYPad;

    if (mayHaveChildren) {
        hitbox.innerPos = [x + INDENT, y + height];
    }

    let width = endX - x + xPad;
    let child = node.args.child;
    let hasChildren = mayHaveChildren && child != null;
    let childrenHeight = 0
    if (hasChildren) {
        let firstChildRid = child[1]
        let firstChild = lookupNode(firstChildRid)
        let [cWidth, cHeight] = drawStack(firstChildRid, firstChild, x + INDENT, y + height, z + 1);
        width = Math.max(width, cWidth + INDENT + xPad);
        childrenHeight = Math.max(MINIMUM_INPUT, cHeight);
    }

    // Render block background
    function renderBackground() {
        drawConnectedRect(x, y, width, height, baseColor, mayConnectTop, mayConnectBottom, 0, mayHaveChildren ? INDENT : 0, true);
        if (mayHaveChildren) {
            drawConnectedRect(x, y + height + childrenHeight, width, END_PART, baseColor, true, mayConnectBottom, INDENT, 0);

            ctx.fillStyle = toCss(baseColor);
            ctx.fillRect(x, y + height - SHADING, INDENT, childrenHeight + (SHADING * 2));
        }
    }

    if (immediatelyRender) {
        renderBackground();
        deferred.forEach(deferred => deferred());
        for (let [oRid, oHeight, commands] of overlays) {
            let yOff = (height / 2) - (oHeight / 2);
            ctx.save()
            ctx.translate(0, yOff);

            commands.forEach(command => command())

            let hitbox = hitboxes[oRid];
            hitbox.self[1] += yOff;

            for (let fieldName in hitbox.fields) {
                hitbox.fields[fieldName][1] += yOff;
            }

            ctx.restore()
        }
    } else {
        deferred.unshift(renderBackground);
    }

    height += (hasChildren ? childrenHeight + END_PART : 0);

    hitbox.z = z;
    hitbox.self = [x, y, width, height];
    hitboxes[rid] = hitbox;

    return [width, height, deferred, overlays];
}

// Draw a stack of nodes starting from one
function drawStack(rid, node, x, y, z) {
    let width = 0;
    let height = 0

    let currentId = rid;
    let current = node;
    while (current != null) {
        let [rWidth, rHeight] = drawNode(currentId, current, x, y + height, true, z);
        width = Math.max(width, rWidth);
        height += rHeight;

        currentId = current.next;
        current = lookupNode(currentId);
    }

    return [width, height];
}

function setBaseCanvasProperties() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    ctx.font = blockFont;

    ctx.imageSmoothingEnabled = false;
    ctx.webkitImageSmoothingEnabled = false;
    ctx.mozImageSmoothingEnabled = false;
}

function pointInRect(px, py, x, y, width, height) {
    return x <= px && px <= x + width &&
        y <= py && py <= y + height;
}

function getNodeAt(x, y) {
    let foundHitbox;
    let foundNodeRid;
    let foundNode;
    let maxZ = -1;
    let maxRc = -1;
    for (let rid in hitboxes) {
        let hitbox = hitboxes[rid]
        let selfHitbox = hitbox.self;

        let isInBox = pointInRect(x, y, selfHitbox[0], selfHitbox[1], selfHitbox[2], selfHitbox[3]);
        if (!isInBox) continue

        ctx.fillText(hitbox.z, selfHitbox[0] + selfHitbox[2] + 64, selfHitbox[1])

        if (maxRc < hitbox.rc) {
            foundNodeRid = rid;
            foundHitbox = hitbox;
            foundNode = lookupNode(rid);
            maxZ = hitbox.z;
            maxRc = hitbox.rc
        }
    }
    return [foundHitbox, foundNodeRid, foundNode];
}

function getFieldAt(x, y, hitbox) {
    for (let fieldName in hitbox.fields) {
        let bb = hitbox.fields[fieldName];

        if (pointInRect(x, y, bb[0], bb[1], bb[2], bb[3]))
            return [bb, fieldName];
    }
    return [null, null]
}

let mouseX = 0;
let mouseY = 0;
let grabbedNodeRid;
let grabbedNode;
let startGrabPos;
let grabOrigin;
let lastDelta = 0;

const DIST_LIMIT = 32;
function getNearestConnectableNode(x, y) {
    let closestDist = Infinity
    let closestRid;
    let closest;
    let isInner = false;
    for (let rid in hitboxes) {
        if (rid === grabbedNodeRid) continue;
        let thisHitbox = hitboxes[rid];
        let thisBb = thisHitbox.self
        let thisNode = lookupNode(rid);
        let thisDefinition = lookupDefinition(thisNode)

        let thisX = thisBb[0];
        let thisY = thisBb[1] + thisBb[3] - 4;

        let dist = Math.hypot(thisX - x, thisY - y);

        ctx.fillStyle = "yellow"
        ctx.fillRect(thisX - 1, thisY - 1, 3, 3)

        let innerPos = thisHitbox.innerPos;
        let innerDist = Infinity
        if (innerPos) {
            ctx.fillStyle = "green"
            ctx.fillRect(innerPos[0] - 1, innerPos[1] - 1, 3, 3)

            innerDist = Math.hypot(innerPos[0] - x, innerPos[1] - y)

            ctx.fillText(innerDist.toFixed(1), innerPos[0], innerPos[1])
        }

        if (!SHAPES[thisDefinition.shape][1]) continue;

        ctx.fillStyle = "white"
        ctx.fillText(dist.toFixed(1), thisX, thisY)

        if (dist < closestDist && dist < DIST_LIMIT && y > thisY) {
            closestDist = dist;
            closestRid = rid;
            closest = thisNode;
            isInner = false;
        }

        if (innerPos && innerDist < closestDist && innerDist < DIST_LIMIT && y > innerPos[1]) {
            closestDist = dist;
            closestRid = rid;
            closest = thisNode;
            isInner = true;
        }
    }

    return [isInner, closestRid, closest];
}

function checkIfChild(parentRid, nodeRid) {
    let parent = lookupNode(parentRid);
    let parentArgs = parent.args;
    for (let argName in parentArgs) {
        let argValue = parentArgs[argName];
        if (argValue[0]) continue;

        let childRid = argValue[1];
        if (childRid === nodeRid) return true;
``
        if (checkIfChild(childRid, nodeRid)) return true;
    }
    return false;
}

function getNearestAvailableField(x, y) {
    let closestDist = Infinity
    let closestRid;
    let closestParent;
    let closestFieldName;

    for (let rid in hitboxes) {
        if (rid === grabbedNodeRid) continue;
        let thisHitbox = hitboxes[rid];

        let thisNode = lookupNode(rid);
        if (checkIfChild(grabbedNodeRid, rid)) continue;

        for (let fieldName in thisHitbox.fields) {
            let field = thisHitbox.fields[fieldName];
            let arg = thisNode.args[fieldName];

            if (!arg[0]) continue;

            let thisX = field[0] + (field[2] / 2);
            let thisY = field[1] + (field[3] / 2);

            let dist = Math.hypot(thisX - x, thisY - y);

            if (dist > DIST_LIMIT) continue;

            if (dist < closestDist) {
                closestDist = dist;
                closestRid = rid;
                closestParent = thisNode;
                closestFieldName = fieldName;
            }
        }
    }
    return [closestFieldName, closestRid, closestParent];
}

function setCursor(type) {
    canvas.style.cursor = type;
}

function setCursorUnlessGrabbing(type) {
    if (grabbedNode) {
        setCursor("grabbing")
    } else {
        setCursor(type);
    }
}

function handleHover() {
    let [hitbox, rid, node] = getNodeAt(mouseX, mouseY);
    hoveredNodeRid = rid;
    hoveredNode = node;

    if (hitbox != null) {
        let [fieldHitbox, fieldName] = getFieldAt(mouseX, mouseY, hitbox);
        hoveredField = fieldName;
        if (fieldHitbox != null) {
            setCursorUnlessGrabbing("text")
        } else {
            setCursorUnlessGrabbing("grab")
        }
    } else {
        setCursorUnlessGrabbing("default")
    }
}

let hoveredNodeRid;
let hoveredNode;
let hoveredField;
function render(delta) {
    let beginFrame = performance.now();
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Keep canvas the same size as the window
    setBaseCanvasProperties()

    // Render nodes
    hitboxes = {};
    let nodes = SCRIPT["operations"];
    for (let key in nodes) {
        let node = nodes[key];
        if (node.parent != null) continue;

        drawStack(key, node, node.x ?? 4, node.y ?? 4, 0);
    }

    // Handle dragging and stuff
    handleHover()

    let grabbedHitbox = hitboxes[grabbedNodeRid];
    if (grabbedNode && grabbedHitbox) {
        let grabbedBb = grabbedHitbox.self;
        let [x, y] = [grabbedBb[0], grabbedBb[1]];

        ctx.fillStyle = "red"
        ctx.fillRect(x - 1, y - 1, 3, 3)

        let definition = lookupDefinition(grabbedNode);
        if (definition.shape !== "input") {
            let [inner, connectableRid, connectable] = getNearestConnectableNode(x, y)
            if (connectable) {
                let hb = inner ? hitboxes[connectableRid].innerPos : hitboxes[connectableRid].self
                drawText("THIS ONE!!!!", hb[0], hb[1], inner ? [255, 0, 0] : [0, 255, 0], "top", "left")
            }
        } else {
            // x + (grabbedBb[2] / 2), y + (grabbedBb[3] / 2)
            let [fieldName, parentRid, parent] = getNearestAvailableField(mouseX, mouseY);
            if (parent) {
                let hb = hitboxes[parentRid].fields[fieldName]
                drawText(fieldName, hb[0], hb[1], [0, 0, 255], "top", "left");
            }
        }

    }

    let endFrame = performance.now();

    drawText(`${(delta - lastDelta).toFixed(1)}ms (synced)`, canvas.width - 4, 4, [205, 214, 244], "top", "right")
    drawText(`${(endFrame - beginFrame).toFixed(1)}ms (frametime)`, canvas.width - 4, 22, [205, 214, 244], "top", "right")

    lastDelta = delta;

    requestAnimationFrame(render);
}

function handleDragging() {
    let dx = (mouseX - grabOrigin[0]);
    let dy = (mouseY - grabOrigin[1]);

    let distance = Math.hypot(dx, dy);

    if (!grabbedNode.parent) {
        grabbedNode.x = startGrabPos[0] + dx;
        grabbedNode.y = startGrabPos[1] + dy;
    } else if (distance > 10) {
        let parent = lookupNode(grabbedNode.parent);
        let child = parent.args.child;
        let hitbox = hitboxes[grabbedNodeRid].self;
        let shape = lookupDefinition(grabbedNode).shape;

        startGrabPos = [hitbox[0], hitbox[1]]

        grabbedNode.parent = null;
        if (child?.[1] === grabbedNodeRid) {
            child[1] = null;
        } else if (shape === "input") {
            for (let argName in parent.args) {
                let argument = parent.args[argName];
                if (argument[1] === grabbedNodeRid) {
                    argument[0] = true;
                    argument[1] = "";
                    break;
                }
            }
        } else {
            parent.next = null;
        }
    }
}

function handleMouseMove(event) {
    mouseX = event.clientX;
    mouseY = event.clientY;

    if (grabbedNode) {
        handleDragging()
    }
}

function handleMouseDown(event) {
    if (hoveredField) {
        console.log(hoveredField);
    } else {
        if (hoveredNode) {
            grabbedNode = hoveredNode;
            grabbedNodeRid = hoveredNodeRid;
            startGrabPos = [hoveredNode.x ?? 0, hoveredNode.y ?? 0];
            grabOrigin = [event.clientX, event.clientY];
        }
    }
}

function resolveLastInStack(rid) {
    let currentId = rid;
    let current = lookupNode(rid);
    while (current.next != null) {
        currentId = current.next
        current = lookupNode(currentId);
    }
    return [currentId, current];
}

function tryNodeConnect(grabbedBb) {
    let [inner, connectableRid, connectable] = getNearestConnectableNode(grabbedBb[0], grabbedBb[1])
    if (!connectable) return;

    let [lastNodeRid, lastNode] = resolveLastInStack(grabbedNodeRid);
    if (inner) {
        let child = connectable.args.child;
        let childRid = child[1];
        if (childRid) {
            let previousChild = lookupNode(childRid);
            previousChild.parent = lastNodeRid;
            lastNode.next = childRid;
        }

        grabbedNode.parent = connectableRid;
        child[1] = grabbedNodeRid;
    } else {
        let previousRid = connectable.next;
        let previous = lookupNode(previousRid);

        grabbedNode.parent = connectableRid;
        connectable.next = grabbedNodeRid;

        lastNode.next = previousRid;
        if (previous)
            previous.parent = lastNodeRid;
    }
}

function tryFieldInsert(grabbedBb) {
    //let x = grabbedBb[0] + (grabbedBb[2] / 2);
    //let y = grabbedBb[1] + (grabbedBb[3] / 2);
    let [fieldName, parentRid, parent] = getNearestAvailableField(mouseX, mouseY);

    if (!parent) return;

    grabbedNode.parent = parentRid;
    parent.args[fieldName] = [false, grabbedNodeRid];
}

function handleMouseUp(event) {
    if (grabbedNode && grabbedNode.parent == null) {
        let grabbedBb = hitboxes[grabbedNodeRid].self;
        let shape = lookupDefinition(grabbedNode).shape;
        if (shape === "input") {
            tryFieldInsert(grabbedBb);
        } else {
            tryNodeConnect(grabbedBb)
        }
    }

    grabbedNodeRid = null;
    grabbedNode = null;
}

export function initRenderer() {
    canvas.addEventListener('mousemove', handleMouseMove);
    canvas.addEventListener('mousedown', handleMouseDown);
    canvas.addEventListener('mouseup', handleMouseUp)

    requestAnimationFrame(render);
}