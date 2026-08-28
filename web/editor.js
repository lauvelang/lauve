const canvas = document.getElementById("workspace-canvas");
const ctx = canvas.getContext("2d");

let blocks = null;
let translations;
let blockFont;

function lighten(v) {
    return Math.min(v * 1.1, 255);
}

function darken(v) {
    return Math.max(v / 1.1, 0);
}

function drawLightedRect(x, y, width, height, r, g, b) {
    ctx.fillStyle = `rgb(${darken(r)}, ${darken(g)}, ${darken(b)})`;
    ctx.fillRect(x, y + height, width, 2);

    ctx.fillStyle = `rgb(${r}, ${g}, ${b})`;
    ctx.fillRect(x, y, width, height);

    ctx.fillStyle = `rgb(${lighten(r)}, ${lighten(g)}, ${lighten(b)})`;
    ctx.fillRect(x, y - 2, width, 2);
}

function drawBlock(x, y, id) {
    let splitId = id.split(":")
    let description = blocks[splitId[0]][splitId[1]]["description"];
    const X_PADDING = 6;
    const Y_PADDING = 8;
    const DIVOT_XOFFSET = 16;
    const DIVOT_WIDTH = 20;
    const DIVOT_YOFFSET = 4;

    let connectableFromBottom = true;
    let connectableFromTop = true;

    let drawCommands = []
    let workingX = x + X_PADDING;
    let workingY = y + Y_PADDING;
    let maxY = y + X_PADDING;
    for (let component of description) {
        switch (component.type) {
            case "label": {
                let translated = translations[id][component.id];
                const textMetrics = ctx.measureText(translated);
                let thisX = workingX;
                let thisY = workingY;
                maxY = Math.max(maxY, workingY + textMetrics.actualBoundingBoxAscent + textMetrics.actualBoundingBoxDescent);

                drawCommands.push(() => {
                    ctx.fillStyle = "white";
                    ctx.fillText(translated, thisX, thisY);
                });

                workingX += textMetrics.width;
            }
        }
    }


    let width = workingX - x + X_PADDING;
    let height = maxY - y + Y_PADDING;

    ctx.fillStyle = "#AAAAFF";
    drawLightedRect(x, y, DIVOT_XOFFSET, height, 170, 170, 255);

    let divotY = y;
    let divotHeight = height + DIVOT_YOFFSET;
    if (connectableFromTop) {
        divotY += DIVOT_YOFFSET;
        divotHeight -= DIVOT_YOFFSET;
    }
    if (!connectableFromBottom) {
        divotHeight -= DIVOT_YOFFSET;
    }

    drawLightedRect(x + DIVOT_XOFFSET, divotY, DIVOT_WIDTH, divotHeight, 170, 170, 255);
    drawLightedRect(x + DIVOT_XOFFSET + DIVOT_WIDTH, y, width - DIVOT_XOFFSET - DIVOT_WIDTH, height, 170, 170, 255);

    drawCommands.forEach(c => c())

    return maxY + Y_PADDING + 3; // 2 for shading
}

function fixCanvasSize() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    ctx.font = blockFont;
    ctx.textBaseline = "top";

    ctx.imageSmoothingEnabled = false;
    ctx.webkitImageSmoothingEnabled = false;
    ctx.mozImageSmoothingEnabled = false;

}

function render() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    fixCanvasSize()
    let y = drawBlock(4, 4, "control:load");
    drawBlock(4, y, "control:load");

    requestAnimationFrame(render);
}

function ready() {
    requestAnimationFrame(render);
}

(async () => {
    const blockPromise = fetch("/blocks.json").then((response) => response.json()).then((data) => {
        blocks = data;
        console.log("Loaded block definitions");
    });

    const translationPromise = fetch("/lang/en_us.json").then((response) => response.json()).then((data) => {
        translations = data;
        console.log("Loaded translations");
    });

    const fontFace = new FontFace('Monocraft', 'url(https://cdn.jsdelivr.net/gh/IdreesInc/Monocraft@main/dist/Monocraft-ttf/Monocraft.ttf)');
    const fontPromise = fontFace.load().then((font) => {
        document.fonts.add(font);
        blockFont = "16px " + font.family + ", monospace";
        console.log("Loaded font");
    });

    await Promise.all([blockPromise, translationPromise, fontPromise]);
    ready();
})();

