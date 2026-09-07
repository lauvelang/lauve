import {Script} from "../script/script";

export let loadedScripts: Script[] = [];
export let script: Script;

export function addScript(newScript: Script) {
    loadedScripts.push(newScript);

    if (!script) script = newScript;
}

export function initState() {
    addScript(Script.newScript());
}