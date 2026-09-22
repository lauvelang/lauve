import {Id, Rectangle} from "../utils";
import {Definition, lookupDefinition, MISSINGNO} from "./definitions";
import {Script} from "./script";
import {script} from "../editor/state";

export class Argument {
    resolved: boolean;
    value: string | any;

    oldResolvedValue: any = null;

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

export function findLastInStack(root: Node): Node {
    let current = root;
    while (current.next) {
        current = script.lookupNode(current.next)!;
    }
    return current;
}

export function findFirstInStack(root: Node): Node {
    let current = root;
    while (current.parent) { current = script.lookupNode(current.parent)!; }
    return current;
}

export class Node {
    id: string; // Unique ID to each node
    opcode: Id; // The type of block this is
    args: Map<string, Argument>; // Any fields this node has
    parent: string | null; // Either the node above or the node that contains (if shape is input) this node
    next: string | null; // The node below this node

    renderBB: Rectangle;
    lastModified: number;
    template: boolean;

    definition: Definition;

    constructor(id: string, opcode: Id, args: Map<string, Argument>, parent: string | null, next: string | null, x: number, y: number, template: boolean = false) {
        this.id = id;
        this.opcode = opcode;
        this.args = args;
        this.parent = parent;
        this.next = next;

        this.renderBB = new Rectangle(x, y, 0, 0);
        this.lastModified = 0; this.markModified();
        this.template = template;

        this.definition = lookupDefinition(this.opcode);
    }

    mayHaveChild(): boolean {
        return this.definition.has_children;
    }

    getFirstChild(script: Script): Node | undefined {
        let cid = this.args.get("child")?.value;
        return script.lookupNode(cid);
    }

    setFirstChild(node: Node | null) {
        let arg = this.args.get("child");
        if (!arg) return;

        arg.value = node ? node.id : null;
    }

    linkNext(next: Node) {
        this.next = next.id;
        next.parent = this.id;
    }

    isPartOfSelf(node: Node): boolean {
        for (let arg of this.args.values()) {
            if (!arg.resolved) {
                if (node.id === arg.value) {
                    return true;
                }

                let argNode = script.lookupNode(arg.value);
                if (argNode && argNode.isPartOfSelf(node)) return true;
            }
        }
        return false;
    }

    markModified() {
        this.lastModified = Date.now();
    }

    erase() {
        for (let arg of this.args.values()) {
            if (!arg.resolved) {
                let argNode = script.lookupNode(arg.value);
                argNode?.erase()
            }
        }

        let next = script.lookupNode(this.next);
        next?.erase();

        script.removeNode(this);
    }

    // Turn into object that only contains needed data
    serialize() {
        let args: any = {}
        this.args.forEach((value, key) => {
            args[key] = [value.resolved, value.value];
        });

        let obj: any = {
            opcode: this.opcode.toString(),
            args: args,
            parent: this.parent,
            next: this.next
        }

        if (this.parent) {
            obj.x = this.renderBB.x;
            obj.y = this.renderBB.y;
        }

        return obj;
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

    static defaultOf(opcode: Id, toolbox: boolean = false): Node {
        let definition: Definition = lookupDefinition(opcode);
        if (!definition) throw new Error(`Unknown opcode: ${opcode}`);

        let args = new Map<string, Argument>();
        for (const part of definition.description) {
            if (part.type !== "input" && part.type !== "option") continue;

            let arg = new Argument(true, part.type === "option" ? part.options[0] : part.sample ?? null);
            args.set(part.id, arg);
        }

        if (definition.has_children)
            args.set("child", new Argument(true, null));

        let id = toolbox ? opcode.toString() : generateNodeId();
        return new Node(id, opcode, args, null, null, 0, 0, toolbox);
    }
}