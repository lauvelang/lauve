import { Id, loadJson } from "../utils";

let translations: any;

export async function lookupForNode(opcode: Id, part: string) {
    let node = translations?.[opcode.namespace]?.[opcode.path];
    return node?.[part] ?? part;
}

export async function lookupGroupName(name: string) {
    return translations?.[name]
}

export async function loadTranslations(lang_code: string) {
    console.log("Loading translations...");
    return loadJson("/lang/" + lang_code + ".json").then(data => translations = data);
}