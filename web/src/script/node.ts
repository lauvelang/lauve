import {Id} from "../utils";
import {Definition, lookupDefinition} from "./definitions";

export class Argument {
    resolved: boolean;
    value: string | any;

    constructor(resolved: boolean, value: any | string) {
        this.resolved = resolved;
        this.value = value;
    }
}

const NODE_ID_LENGTH = 12;
const NODE_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-=_+()*&^%$#@!,.<>?/;:";
export function generateNodeId() {
    let result = '';
    for (let i = 0; i < NODE_ID_LENGTH; i++) {
        result += NODE_CHARSET.charAt(Math.floor(Math.random() * NODE_CHARSET.length));
    }
    return result;
}

export class Node {
    id: string; // Unique ID to each node
    opcode: Id; // The type of block this is
    args: Map<string, Argument>; // Any fields this node has
    parent: string | null; // Either the node above or the node that contains (if shape is input) this node
    next: string | null; // The node below this node

    x: number; // The location of this node in the editor
    y: number; // Not used by the runtime

    constructor(id: string, opcode: Id, args: Map<string, Argument>, parent: string | null, next: string | null, x: number, y: number) {
        this.id = id;
        this.opcode = opcode;
        this.args = args;
        this.parent = parent;
        this.next = next;
        this.x = x;
        this.y = y;
    }

    static fromJson(id: string, data: any): Node {
        let parsedId = Id.fromString(data.opcode);
        let args = new Map<string, Argument>();
        for (const field in data.args) {
            let [resolved, value] = data.args[field];
            args.set(field, new Argument(resolved, value));
        }

        return new Node(id, parsedId, args, data.parent, data.next, data.x ?? 0, data.y ?? 0);
    }

    static defaultOf(opcode: Id): Node {
        let definition: Definition = lookupDefinition(opcode);
        if (!definition) throw new Error(`Unknown opcode: ${opcode}`);

        let args = new Map<string, Argument>();
        for (const part of definition.description) {
            if (part.type !== "input") continue;

            let arg = new Argument(true, part.sample);
            args.set(part.id, arg);
        }

        let id = generateNodeId();
        return new Node(id, opcode, args, null, null, 0, 0);
    }
}