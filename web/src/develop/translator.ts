import {loadTranslations, lookupForNode, TRANSLATIONS} from "../editor/translations";
import {Definition, DEFINITIONS, loadDefinitions} from "../script/definitions";
import {Id, saveObject} from "../utils";
import {loadFont} from "../render/font";

function addEditorWidgetThing(id: Id, definition: Definition) {
    const container = document.createElement("div");

    const header = document.createElement("h1");
    header.innerText = id.toString();
    container.appendChild(header);

    for (let part of definition.description) {
        switch (part.type) {
            case "label": {
                let label = document.createElement("span");
                label.innerText = part.id + ": ";

                let input = document.createElement("input");
                input.type = "text";
                input.addEventListener("blur", () => {
                    if (!TRANSLATIONS[id.namespace][id.path]) TRANSLATIONS[id.namespace][id.path] = {};
                    if (input.value) {
                        TRANSLATIONS[id.namespace][id.path][part.id] = input.value;
                    } else {
                        delete TRANSLATIONS[id.namespace][id.path][part.id];
                    }

                });

                label.appendChild(input);

                let translated = TRANSLATIONS[id.namespace][id.path]?.[part.id];
                input.value = translated ?? "";

                let button = document.createElement("button");
                button.innerText = "Set";
                label.appendChild(button);

                container.appendChild(label);

                break
            }
            case "option": {
                break
            }
            default: {}
        }

    }

    document.body.appendChild(container);
}

const LANG_CODE = "en_us";

function main() {
    document.querySelector(".export")?.addEventListener("click", (e) => {
        saveObject(TRANSLATIONS, LANG_CODE + ".json", e, true)
    })

    for (let namespace in DEFINITIONS) {
        let group = DEFINITIONS[namespace];
        for (let name in group) {
            let definition = group[name];
            if (Array.isArray(definition)) continue;

            addEditorWidgetThing(new Id(namespace, name), definition);
        }
    }
}


(async () => {
    await loadFont()
    await loadDefinitions();
    await loadTranslations(LANG_CODE);

    main();
})();