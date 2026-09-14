import {Script} from "../script/script";

export let loadedScripts: Script[] = [];
export let script: Script;

export function addScript(newScript: Script) {
    loadedScripts.push(newScript);

    if (!script) script = newScript;
}

export function promptLoad(event: KeyboardEvent) {
    const input = document.createElement('input');

    input.type = "file";
    input.accept = "text/plain,.luv";
    input.onchange = e => {
        let target = e.target as HTMLInputElement;
        if (!target?.files) return;

        let file = target.files[0];
        let reader = new FileReader();
        reader.readAsText(file,'UTF-8');

        reader.onload = readerEvent => {
            try {
                let target = readerEvent.target?.result;
                let data = JSON.parse(target as string);

                let newScript = Script.fromJson(file.name, data);
                loadedScripts.push(newScript);
                script = newScript;
            } catch (e) {
                throw e;
            }
        }
    };

    input.click();
    event.preventDefault();
}

export function initState() {
    addScript(Script.newScript());

    // Keyboard shortcuts
    window.addEventListener('keydown', (event) => {
        if (!event.ctrlKey) return;

        switch (event.key) {
            case 'o': {
                promptLoad(event);
                break
            }
        }
    })
}