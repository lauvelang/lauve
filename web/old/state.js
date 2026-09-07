/*class Argument {
    constructor(resolved, value) {
        this.resolved = resolved;
        this.value = value;
    }
}

class Node {
    constructor(opcode, parent, next, args) {
        this.opcode = opcode;
        this.parent = parent;
        this.next = next;
        this.args = args;
    }
}*/

const separator = ":";
export class Id {
    constructor(namespace, path) {
        this.namespace = namespace;
        this.path = path;
    }

    static fromString(string) {
        let split = string.split(separator);
        if (split.length !== 2) {
            throw new Error("Id must be in format namespace:path");
        }

        return new Id(split[0], split[1]);
    }

    toString() {
        return this.namespace + separator + this.path;
    }
}

const SCRIPT_KEY = "last_script";
export let SCRIPT = {
    "operations": {}
}

let lastScript = JSON.parse(localStorage.getItem(SCRIPT_KEY) ?? "{}");
if (lastScript && lastScript["operations"]) {
    SCRIPT = lastScript;
}

function autosave() {
    console.log("Saved script!");
    console.log(SCRIPT)
    localStorage.setItem(SCRIPT_KEY, JSON.stringify(SCRIPT));
}

window.as = autosave;

window.addEventListener('beforeunload', function() {
    console.log("before unload")
    autosave()
});

setInterval(autosave, 60 * 5 * 1000);

window.addEventListener("error", function () {
    localStorage.setItem("backup_last_script", localStorage.getItem(SCRIPT_KEY))
    localStorage.removeItem(SCRIPT_KEY)
})

export function replaceScript(data) {
    SCRIPT = data;
}

export function lookupNode(node) {
    return SCRIPT["operations"][node];
}

export function lookupDefinition(node) {
    let opcode = Id.fromString(node.opcode);
    let group = window.blocks[opcode.namespace];
    return group[opcode.path]
}