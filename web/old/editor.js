import { initRenderer } from './render.js';

window.blocks = null;
window.translations = null;
window.blockFont = null;
window.miniFont = null;

(async () => {
    const blockPromise = fetch("/blocks.json").then((response) => response.json()).then((data) => {
        window.blocks = data;
        console.log("Loaded block definitions");
    });

    const translationPromise = fetch("/lang/en_us.json").then((response) => response.json()).then((data) => {
        window.translations = data;
        console.log("Loaded translations");
    });

    const fontFace = new FontFace('Monocraft', 'url(https://cdn.jsdelivr.net/gh/IdreesInc/Monocraft@main/dist/Monocraft-ttf/Monocraft.ttf)');
    const fontPromise = fontFace.load().then((font) => {
        document.fonts.add(font);
        let family = font.family + ", monospace";
        window.blockFont = "18px " + family;
        window.miniFont = "9px " + family;
        //window.blockFont = "18px monospace";
        console.log("Loaded font");
    });

    await Promise.all([blockPromise, translationPromise, fontPromise]);
    initRenderer();
})();

