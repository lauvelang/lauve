import {Id, lookupNode} from "./state.js";

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
function drawShadedRect(x, y, width, height, color, top = true, bottom = true) {
    // TODO: color theorists would execute me if they saw this
    function lighten(v) {
        return Math.min(v * 1.1, 255);
    }

    function darken(v) {
        return Math.max(v / 1.1, 0);
    }

    let [r, g, b] = color;

    ctx.fillStyle = toCss(color);
    ctx.fillRect(x, y, width, height);

    if (top) {
        ctx.fillStyle = toCss([lighten(r), lighten(g), lighten(b)]);
        ctx.fillRect(x, y, width, SHADING);
    }

    if (bottom) {
        ctx.fillStyle = toCss([darken(r), darken(g), darken(b)]);
        ctx.fillRect(x, y + height - SHADING, width, SHADING);
    }
}

const TEXT = [255, 255, 255];
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
const NODE_YPAD = 8; // Padding of block in each vertical direction
const CONNECTOR_START = 16; // How far into the block the connector bit starts
const CONNECTOR_WIDTH = 20; // How wide the connector reaches
const CONNECTOR_HEIGHT = 4; // How much the connector moves vertically
const PART_MARGIN = 4;
const INDENT = 12; // How far each nest goes inward
const END_PART = 24; // The blank end of child having blocks

// Draw a rectangle with connector
function drawConnectedRect(x, y, width, height, color, top, bottom, topOffset, bottomOffset) {
    if (top) {
        let start = CONNECTOR_START + topOffset;
        let postConnector = start + CONNECTOR_WIDTH
        drawShadedRect(x, y, start, height, color); // before connector
        drawShadedRect(x + start, y + CONNECTOR_HEIGHT, CONNECTOR_WIDTH, height - CONNECTOR_HEIGHT, color) // connector
        drawShadedRect(x + postConnector, y, width - postConnector, height, color) // after connector
    } else {
        drawShadedRect(x, y, width, height, color)
    }

    if (bottom) {
        let start = CONNECTOR_START + bottomOffset;
        drawShadedRect(x + start, y + height - SHADING, CONNECTOR_WIDTH, CONNECTOR_HEIGHT + SHADING, color, false, true)
    }
}

// Draw a node in the script format
function drawNode(node, x, y) {
    let opcode = Id.fromString(node.opcode);
    let group = window.blocks[opcode.namespace];
    let definition = group[opcode.path];
    let description = definition.description;

    let baseColor = group.color;

    let nodeShape = definition.shape;
    let [mayConnectTop, mayConnectBottom] = SHAPES[nodeShape];

    let mayHaveChildren = definition.has_children;
    let deferred = []; // We need to wait before rendering a lot of the node

    let doubleYPad = NODE_YPAD * 2;

    let height = 0;
    let endX = x + NODE_XPAD;
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
                const textMetrics = ctx.measureText(component.id);
                let thisWidth = textMetrics.width + (padding * 2);
                let thisHeight = 20;

                deferred.push(() => {
                    drawShadedRect(thisX, y + (height / 2) - (thisHeight / 2), thisWidth, thisHeight, [255, 255, 255])
                    drawText(component.id, thisX + padding, y + (height / 2), [0, 0, 0], "middle");
                })

                endX += thisWidth;
            }
        }
        endX += PART_MARGIN;
    }
    endX -= PART_MARGIN;
    height += doubleYPad;

    let width = endX - x + NODE_XPAD;
    let child = node.args.child;
    let hasChildren = mayHaveChildren && child != null;
    let childrenHeight = 0
    if (hasChildren) {
        let firstChild = lookupNode(child[1])
        let [cWidth, cHeight] = drawStack(firstChild, x + INDENT, y + height);
        width = Math.max(width, cWidth + INDENT + NODE_XPAD);
        childrenHeight += cHeight;
    }

    // Render block background
    drawConnectedRect(x, y, width, height, baseColor, mayConnectTop, mayConnectBottom, 0, mayHaveChildren ? INDENT : 0);
    if (mayHaveChildren) {
        ctx.fillStyle = toCss(baseColor);
        ctx.fillRect(x, y + height, INDENT, childrenHeight);
        drawConnectedRect(x, y + height + childrenHeight, width, END_PART, baseColor, true, mayConnectBottom, INDENT, 0);
    }

    // Render all deferred items
    deferred.forEach(deferred => deferred());

    drawText(`${opcode}`, x + width + 8, y + (height / 2), [255, 255, 255], "middle");

    return [width, height + (hasChildren ? childrenHeight + END_PART : 0)];
}

// Draw a stack of nodes starting from one
function drawStack(node, x, y) {
    let width = 0;
    let height = 0

    let current = node;
    while (current != null) {
        let [rWidth, rHeight] = drawNode(current, x, y + height);
        width = Math.max(width, rWidth);
        height += rHeight;

        current = lookupNode(current.next);
    }

    return [width, height];
}

// block spacing stuff
/*
const X_PADDING = 6;
const Y_PADDING = 8;
const DIVOT_XOFFSET = 16;
const DIVOT_WIDTH = 20;
const DIVOT_YOFFSET = 4;

const ITEM_MARGIN = 8;
const INPUT_SIZE = 48;

function drawBlock(x, y, id) {
    let splitId = id.split(":")
    let namespace = splitId[0];
    let path = splitId[1];

    let group = blocks[namespace];
    let color = group["color"]

    let definition = group[splitId[1]];
    let description = definition["description"];

    let connectivity = SHAPES[definition["shape"]]
    let connectableFromTop = connectivity[0]
    let connectableFromBottom = connectivity[1]

    let drawCommands = []
    let workingX = x + X_PADDING;
    let workingY = y + Y_PADDING;
    let maxY = y + Y_PADDING;
    for (let component of description) {
        let thisX = workingX;
        let thisY = workingY;
        switch (component.type) {
            case "label": {
                let translated = translations[namespace][path][component.id];
                const textMetrics = ctx.measureText(translated);
                maxY = Math.max(maxY, thisY + textMetrics.actualBoundingBoxAscent + textMetrics.actualBoundingBoxDescent);

                drawCommands.push(() => {
                    ctx.fillStyle = "white";
                    ctx.fillText(translated, thisX, thisY);
                });

                workingX += textMetrics.width;
                break
            }
            case "input": {
                drawCommands.push(() => {
                    drawLightedRect(thisX, thisY, INPUT_SIZE, maxY - y - Y_PADDING + 1, 255, 255, 255)
                })
                workingX += INPUT_SIZE;
                break;
            }
        }
        workingX += ITEM_MARGIN;
    }
    workingX -= ITEM_MARGIN;


    let width = workingX - x + X_PADDING;
    let height = maxY - y + Y_PADDING;

    drawLightedRect(x, y, DIVOT_XOFFSET, height, color[0], color[1], color[2]);

    let divotY = y;
    let divotHeight = height + DIVOT_YOFFSET;
    if (connectableFromTop) {
        divotY += DIVOT_YOFFSET;
        divotHeight -= DIVOT_YOFFSET;
    }
    if (!connectableFromBottom) {
        divotHeight -= DIVOT_YOFFSET;
    }

    drawLightedRect(x + DIVOT_XOFFSET, divotY, DIVOT_WIDTH, divotHeight, color[0], color[1], color[2]);
    drawLightedRect(x + DIVOT_XOFFSET + DIVOT_WIDTH, y, width - DIVOT_XOFFSET - DIVOT_WIDTH, height, color[0], color[1], color[2]);

    drawCommands.forEach(c => c())

    return maxY + Y_PADDING + 4; // 2 for shading
}
*/



function setBaseCanvasProperties() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    ctx.font = blockFont;

    ctx.imageSmoothingEnabled = false;
    ctx.webkitImageSmoothingEnabled = false;
    ctx.mozImageSmoothingEnabled = false;
}

let mouseX = 0;
let mouseY = 0;
let mouseDown = false;
let lastDelta = 0;
function render(delta) {
    let beginFrame = performance.now();
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    setBaseCanvasProperties()

    drawStack(lookupNode("lWYqMdh9HS"), mouseX, mouseY);

    let endFrame = performance.now();

    drawText(`${(delta - lastDelta).toFixed(1)}ms (synced)`, canvas.width - 4, 4, [255, 255, 255], "top", "right")
    drawText(`${(endFrame - beginFrame).toFixed(1)}ms (frametime)`, canvas.width - 4, 22, [255, 255, 255], "top", "right")

    lastDelta = delta;

    requestAnimationFrame(render);
}

export function initRenderer() {
    window.addEventListener('mousemove', (event) => {
        if (mouseDown) {
            mouseX = event.clientX;
            mouseY = event.clientY;
        }
    });

    window.addEventListener('mousedown', (event) => {
        mouseDown = true;
    })

    window.addEventListener('mouseup', (event) => {
        mouseDown = false;
    })

    requestAnimationFrame(render);
}