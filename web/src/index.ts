import {loadFont} from "./render/font";
import {loadDefinitions} from "./script/definitions";
import {loadTranslations} from "./editor/translations";

import {initState} from "./editor/state";
import {initRenderer} from "./render/render";
import {startLoading, stopLoading} from "./render/throbber";

(async function main() {
    startLoading();
    let start = performance.now();

    // Work that can be initialized asynchronously (mainly fetching resources)
    await Promise.all([
        loadFont(),
        loadDefinitions(),
        loadTranslations("en_us"), // TODO: Language support
    ]);

    initState();
    initRenderer();

    let delta = performance.now() - start;
    stopLoading();
    console.log(`Loaded in ${delta.toFixed(1)}ms`);
})();