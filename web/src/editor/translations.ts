import { Id, loadJson } from "../utils";

export let TRANSLATIONS: any;

export function lookupForNode(opcode: Id, part: string) {
    let node = TRANSLATIONS?.[opcode.namespace]?.[opcode.path];
    return node?.[part] ?? part;
}

export function lookupForOption(opcode: Id, part: string, selected: string): string {
    let map = lookupForNode(opcode, part);
    return map[selected];
}

export function lookupForGroup(name: string, key: string): string {
    return TRANSLATIONS?.[name][key] ?? key
}

export function lookupGroupName(name: string): string {
    return lookupForGroup(name, "_name")
}

export async function loadTranslations(lang_code: string) {
    console.log("Loading translations...");
    return loadJson("/lang/" + lang_code + ".json").then(data => TRANSLATIONS = data);
}