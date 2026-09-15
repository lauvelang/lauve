import { Id, loadJson } from "../utils";

const URL = "/blocks.json"

export enum NodeShape {
    START = "start",
    NORMAL = "normal",
    END = "end",
    INPUT = "input"
}

export const NodeShapeConnectivity: Record<NodeShape, { top: boolean, bottom: boolean }> = {
    [NodeShape.START]: { top: false, bottom: true },
    [NodeShape.NORMAL]: { top: true, bottom: true },
    [NodeShape.END]: { top: true, bottom: false },
    [NodeShape.INPUT]: { top: false, bottom: false }
};

export enum InputController {
    ANY = "any",
    VARIABLE = "variable",
    NUMBER = "number",
    STRING = "string",
    BOOLEAN = "boolean",
    SELECT = "select"
}

export type Part = {
    id: string
} & (
    { type: 'label' } |
    { type: 'input', controller: InputController, sample: string } |
    { type: 'option', options: string[] } );

export type Definition = {
    has_children: boolean,
    shape: NodeShape,
    description: Part[]
}

export let DEFINITIONS: { [namespace: string]: { [path: string]: Definition | [number, number, number] } } = {};
export const MISSINGNO: Id = new Id("debug", "missingno");

export function lookupDefinition(id: Id): Definition {
    return <Definition> DEFINITIONS[id.namespace][id.path];
}

export function lookupColor(namespace: string): [number, number, number] {
    return <[number, number, number]> DEFINITIONS[namespace]["_color"] ?? [0, 0, 0];
}

export async function loadDefinitions() {
    console.log("Loading definitions...");
    return loadJson(URL).then(data => DEFINITIONS = data);
}