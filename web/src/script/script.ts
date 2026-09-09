import {Node} from "./node";
import {Id} from "../utils";

import {v4 as uuidv4} from 'uuid';

export class Script {
    runtimeUuid: string;
    name: string;
    nodes: Map<string, Node>;
    camera: [number, number];

    constructor(name: string) {
        this.runtimeUuid = uuidv4();
        this.name = name;
        this.nodes = new Map<string, Node>();
        this.camera = [0, 0]
    }

    addNode(node: Node) {
        this.nodes.set(node.id, node);
    }

    setCamera(x: number, y: number) {
        this.camera = [x, y];
    }

    static fromJson(name: string, data: any): Script {
        let script = new Script(name);
        for (const id in data.operations) {
            let node = Node.fromJson(id, data.operations[id]);
            script.addNode(node);
        }

        return script;
    }

    // TODO: hardcoded :(
    private static ON_LOAD_ID = new Id("control", "load");
    private static LOG_ID = new Id("debug", "log");
    static newScript(): Script {
        let script = new Script("script.luv");

        let load = Node.defaultOf(Script.ON_LOAD_ID);
        script.addNode(load);

        let log = Node.defaultOf(Script.LOG_ID);
        log.parent = load.id;
        load.next = log.id;
        script.addNode(log);

        return script;
    }

    lookupNode(key: string | null): Node | undefined {
        return key ? this.nodes.get(key) : undefined;
    }
}