const throbber = document.querySelector(".throbber") as HTMLElement;

export function startLoading() {
    throbber.style.opacity = "100%";
    throbber.style.pointerEvents = "auto";
}

export function stopLoading() {
    throbber.style.opacity = "0%";
    throbber.style.pointerEvents = "none";
}