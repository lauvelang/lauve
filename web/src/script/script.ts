import {Node} from "./node";
import {Id} from "../utils";

import {v4 as uuidv4} from 'uuid';

export class Script {
    runtimeUuid: string;
    name: string;
    nodes: Map<string, Node>;

    constructor(name: string) {
        this.runtimeUuid = uuidv4();
        this.name = name;
        this.nodes = new Map<string, Node>();
    }

    addNode(node: Node) {
        this.nodes.set(node.id, node);
    }

    static fromJson(name: string, data: any): Script {
        let script = new Script(name);
        for (const id of data.operations) {
            let node = Node.fromJson(id, data.operations[id]);
            script.addNode(node);
        }

        return script;
    }

    // TODO: hardcoded :(
    private static ON_LOAD_ID = new Id("control", "load");
    static newScript(): Script {
        let script = new Script("script.luv");

        let load = Node.defaultOf(Script.ON_LOAD_ID);
        script.addNode(load);

        return script;
    }
}