import { Id, loadJson } from "../utils";

let translations: any;

export function lookupForNode(opcode: Id, part: string) {
    let node = translations?.[opcode.namespace]?.[opcode.path];
    return node?.[part] ?? part;
}

export function lookupForOption(opcode: Id, part: string, selected: string): string {
    let map = lookupForNode(opcode, part);
    return map[selected];
}

export function lookupGroupName(name: string): string {
    return translations?.[name]["_name"]
}

export async function loadTranslations(lang_code: string) {
    console.log("Loading translations...");
    return loadJson("/lang/" + lang_code + ".json").then(data => translations = data);
}