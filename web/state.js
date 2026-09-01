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

export const SCRIPT = {
    "operations": {
        "lWYqMdh9HS": {
            "opcode": "control:load",
            "args": {},
            "next": "LSRvYa31tF",
            "x": 32,
            "y": 32,
        },
        "LSRvYa31tF": {
            "opcode": "variable:set",
            "args": {
                "key": [true, "i"],
                "value": [true, 0],
                "global": [true, false]
            },
            "next": "9oTLVGQ16B",
            "parent": "lWYqMdh9HS"
        },
        "B1zZq3Mzus": {
            "parent": "r6HviHTRMH",
            "opcode": "variable:get",
            "args": {
                "key": [true, "i"]
            }
        },
        "zN6GNNLLlo": {
            "parent": "l4ka9cVHxW",
            "opcode": "variable:get",
            "args": {
                "key": [true, "i"]
            }
        },
        "GWwLx9zODB": {
            "parent": "IT4q7XS2hj",
            "opcode": "variable:get",
            "args": {
                "key": [true, "i"]
            }
        },
        "r6HviHTRMH": {
            "parent": "0py3dYpjbj",
            "opcode": "condition:equals",
            "args": {
                "first": [false, "B1zZq3Mzus"],
                "second": [true, 10]
            }
        },
        "0py3dYpjbj": {
            "parent": "9oTLVGQ16B",
            "opcode": "condition:not",
            "args": {
                "value": [false, "r6HviHTRMH"]
            }
        },
        "9oTLVGQ16B": {
            "opcode": "control:while",
            "args": {
                "condition": [false, "0py3dYpjbj"],
                "child": [true, "lHh2WCQauc"]
            },
            "parent": "LSRvYa31tF",
            "next": "7YxHoPB139"
        },
        "l4ka9cVHxW": {
            "parent": "lHh2WCQauc",
            "opcode": "math:add",
            "args": {
                "first": [false, "zN6GNNLLlo"],
                "second": [true, 1]
            }
        },
        "lHh2WCQauc": {
            "opcode": "variable:set",
            "args": {
                "key": [true, "i"],
                "value": [false, "l4ka9cVHxW"],
                "global": [true, false]
            },
            "parent": "9oTLVGQ16B",
            "next": "IT4q7XS2hj"
        },
        "IT4q7XS2hj": {
            "opcode": "debug:log",
            "args": {
                "text": [false, "GWwLx9zODB"]
            },
            "parent": "lHh2WCQauc"
        },
        "7YxHoPB139": {
            "opcode": "debug:log",
            "args": {
                "text": [true, "done"]
            },
            "parent": "9oTLVGQ16B"
        }
    }
}

export function lookupNode(node) {
    return SCRIPT["operations"][node];
}

export function lookupDefinition(node) {
    let opcode = Id.fromString(node.opcode);
    let group = window.blocks[opcode.namespace];
    return group[opcode.path]
}