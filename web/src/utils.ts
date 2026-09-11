// ID
export class Id {
    static SEPARATOR: string = ":";

    namespace: string;
    path: string;

    constructor(namespace: string, path: string) {
        this.namespace = namespace;
        this.path = path;
    }

    static fromString(string: string) {
        let split = string.split(Id.SEPARATOR);
        if (split.length !== 2) {
            throw new Error("Id must be in format namespace:path");
        }

        return new Id(split[0], split[1]);
    }

    toString() {
        return this.namespace + Id.SEPARATOR + this.path;
    }
}

// Fetch a URL and parse it as JSON
export async function loadJson(source: string): Promise<any> {
    return fetch(source).then(res => res.json());
}

export function pointInBounds(x: number, y: number, width: number, height: number, px: number, py: number) {
    return x <= px && px <= x + width &&
        y <= py && py <= y + height;
}

export class Rectangle {
    x: number;
    y: number;
    width: number;
    height: number;

    constructor(x: number, y: number, width: number, height: number) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    setPos(x: number, y: number) {
        this.x = x;
        this.y = y;
    }

    setSize(width: number, height: number) {
        this.width = width;
        this.height = height;
    }

    isInside(x: number, y: number) {
        return pointInBounds(this.x, this.y, this.width, this.height, x, y)
    }
}