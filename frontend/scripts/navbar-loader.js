document.addEventListener("DOMContentLoaded", () => {
    const headerContainer = document.getElementById("global-header");
    console.log(headerContainer);
    if (headerContainer) {

        headerContainer.innerHTML = `
            <nav class="navbar">
                <div class="nav-logo">
                    <a href="">Patinhas<span>Felizes</span></a>
                </div>

                <ul class="nav-links">
                    <li><a href="atendente.html" id="link-atendente">Atendente</a></li>
                    <li><a href="cliente.html" id="link-cliente">Cliente</a></li>
                    <li><a href="gestor.html" id="link-gestor">Gestor</a></li>
                    <li><a href="veterinario.html" id="link-veterinario">Veterinario</a></li>
                </ul>

                <!-- <div class="nav-cta">
                    <a href="#" class="btn-cta">Começar</a>
                </div> -->
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
    } else if (page === "cliente.html") {
        document.getElementById("link-cliente")?.classList.add("active");
    } else if (page === "gestor.html") {
        document.getElementById("link-gestor")?.classList.add("active");
    } else if (page === "veterinario.html") {
        document.getElementById("link-veterinario")?.classList.add("active");
    }
}