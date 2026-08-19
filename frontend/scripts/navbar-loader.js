document.addEventListener("DOMContentLoaded", () => {
    const headerContainer = document.getElementById("global-header");
    if (headerContainer) {

        headerContainer.innerHTML = `
            <nav class="navbar">
                <div class="nav-logo">
                    <a href="">
                        <span class="nav-logo-mark">
                            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M8.5 8.2c1.05 0 1.9-1.1 1.9-2.45S9.55 3.3 8.5 3.3s-1.9 1.1-1.9 2.45 .85 2.45 1.9 2.45zm7 0c1.05 0 1.9-1.1 1.9-2.45S16.55 3.3 15.5 3.3s-1.9 1.1-1.9 2.45 .85 2.45 1.9 2.45zM4.8 12.9c.95 0 1.7-1 1.7-2.2s-.75-2.2-1.7-2.2-1.7 1-1.7 2.2 .75 2.2 1.7 2.2zm14.4 0c.95 0 1.7-1 1.7-2.2s-.75-2.2-1.7-2.2-1.7 1-1.7 2.2 .75 2.2 1.7 2.2zM12 11.4c-2.3 0-4.85 1.9-5.35 4.15-.35 1.55.65 2.75 2.05 2.75.85 0 1.4-.35 2.2-.35s1.35.35 2.2.35c1.4 0 2.4-1.2 2.05-2.75-.5-2.25-3.05-4.15-5.15-4.15z"/></svg>
                        </span>
                        Patinhas<span>Felizes</span>
                    </a>
                </div>

                <ul class="nav-links">
                    <li><a href="atendente.html" id="link-atendente">Atendente</a></li>
                    <li><a href="index.html" id="link-cliente">Cliente</a></li>
                    <li><a href="gestor.html" id="link-gestor">Gestor</a></li>
                    <li><a href="veterinario.html" id="link-veterinario">Veterinario</a></li>
                </ul>
            </nav>
        `;
        marcarLinkAtivo();
    }
});

function marcarLinkAtivo() {
    const path = window.location.pathname;
    const page = path.split("/").pop();

    if (page === "atendente.html" || page === "") {
        document.getElementById("link-atendente")?.classList.add("active");
    } else if (page === "index.html") {
        document.getElementById("link-cliente")?.classList.add("active");
    } else if (page === "gestor.html") {
        document.getElementById("link-gestor")?.classList.add("active");
    } else if (page === "veterinario.html") {
        document.getElementById("link-veterinario")?.classList.add("active");
    }
}
