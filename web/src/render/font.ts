export let FONT = "18px monospace";
export let SMALL_FONT = "9px monospace";

const FONT_FACE = new FontFace(
    "Monocraft",
    'url(https://cdn.jsdelivr.net/gh/IdreesInc/Monocraft@main/dist/Monocraft-ttf/Monocraft.ttf)'
);

function formatFamily(family: string, size: number, fallback: string = "monospace") {
    return `${size}px ${family}, ${fallback}`;
}

export async function loadFont() {
    console.log("Loading font...")
    return FONT_FACE.load().then((face) => {
        document.fonts.add(face);

        FONT = formatFamily(face.family, 18)
        SMALL_FONT = formatFamily(face.family, 9)
    });
}