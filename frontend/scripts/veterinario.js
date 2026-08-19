// scripts/veterinario.js

document.addEventListener("DOMContentLoaded", () => {
    const actionButtons = document.querySelectorAll(".direct-action");

    const panel = document.getElementById("dynamic-panel");
    const panelTitle = document.getElementById("panel-title");
    const panelDynamicBody = document.getElementById("panel-dynamic-body");

    actionButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const title = btn.getAttribute("data-title");
            const templateId = btn.getAttribute("data-template");

            panelTitle.innerText = title;
            panelDynamicBody.innerHTML = "";

            const template = document.getElementById(templateId);
            if (template) {
                const clone = template.content.cloneNode(true);
                panelDynamicBody.appendChild(clone);
            }

            panel.classList.add("active-panel");
            setTimeout(() => panel.scrollIntoView({ behavior: 'smooth', block: 'start' }), 50);

            if (templateId === "tmpl-prontuario") configurarProntuario();
            if (templateId === "tmpl-agenda") configurarAgenda();
            // tmpl-atendimento-atual e tmpl-horarios continuam como mock
        });
    });

    const closePanel = () => panel.classList.remove("active-panel");
    document.getElementById("close-panel")?.addEventListener("click", closePanel);
    panel.addEventListener("click", (e) => {
        if (e.target.closest(".close-panel-action")) closePanel();
    });
});

// ==========================================================================
// Histórico de Animais (Prontuário)
// ==========================================================================
// LIMITAÇÃO DO BACKEND: não existe endpoint /animais. A única forma de
// listar animais é extraindo nroAnimal/nomeAnimal dos atendimentos já
// cadastrados (GET /atendimentos). Ou seja, um animal só aparece aqui
// depois de já ter tido pelo menos um atendimento.
function configurarProntuario() {
    const input = document.getElementById("filtro-prontuario-texto");
    const btnBuscar = document.getElementById("btn-buscar-prontuario");
    const lista = document.getElementById("lista-prontuario");

    async function buscar() {
        lista.innerHTML = '<li class="data-item">Buscando...</li>';
        try {
            const nomeFiltro = input.value.trim().toLowerCase();

            const atendimentos = await api.get("/atendimentos");
            const animaisMap = new Map();
            (atendimentos || []).forEach(a => {
                if (a.nroAnimal && !animaisMap.has(a.nroAnimal)) {
                    animaisMap.set(a.nroAnimal, {
                        nroAnimal: a.nroAnimal,
                        nome: a.nomeAnimal || ("Animal " + a.nroAnimal)
                    });
                }
            });

            let animais = Array.from(animaisMap.values());
            if (nomeFiltro) {
                animais = animais.filter(a => a.nome.toLowerCase().includes(nomeFiltro));
            }

            lista.innerHTML = "";
            if (animais.length === 0) {
                lista.innerHTML = '<li class="data-item">Nenhum animal encontrado.</li>';
                return;
            }

            for (const a of animais) {
                const li = document.createElement("li");
                li.className = "data-item";
                li.innerHTML = `
                    <div class="item-info">
                        <strong>${a.nome}</strong>
                        <span>ID: ${a.nroAnimal}</span>
                    </div>
                    <div class="item-actions">
                        <button class="btn-secondary btn-sm">Ver Histórico</button>
                    </div>
                `;

                li.querySelector("button").addEventListener("click", async () => {
                    try {
                        const historico = await api.get("/atendimentos", { nroAnimal: a.nroAnimal });
                        if (historico && historico.length > 0) {
                            const historicoStr = historico.map(h =>
                                `- ${h.ini_dataAtendimento || 'N/A'}: ${h.observacoes || 'Atendimento'}`
                            ).join('\n');
                            alert(`📋 Histórico de ${a.nome}:\n\n${historicoStr}`);
                        } else {
                            alert(`📋 ${a.nome} não possui atendimentos registrados.`);
                        }
                    } catch (err) {
                        mostrarErroApi(err);
                    }
                });
                lista.appendChild(li);
            }
        } catch (err) {
            lista.innerHTML = '<li class="data-item">Erro ao buscar animais.</li>';
            mostrarErroApi(err);
        }
    }

    btnBuscar.addEventListener("click", buscar);
    input.addEventListener("keydown", (e) => { if (e.key === "Enter") buscar(); });
}

// ==========================================================================
// Agenda do Veterinário
// ==========================================================================
async function configurarAgenda() {
    const inputData = document.getElementById("filtro-agenda-data");
    const lista = document.getElementById("lista-agenda");

    async function carregar() {
        lista.innerHTML = '<li class="data-item">Carregando...</li>';
        try {
            const todos = await api.get("/atendimentos");
            const dataFiltro = inputData.value;

            let filtrados = todos || [];
            if (dataFiltro) {
                filtrados = filtrados.filter(a => {
                    if (!a.ini_dataAtendimento) return false;
                    return a.ini_dataAtendimento.startsWith(dataFiltro);
                });
            }

            lista.innerHTML = "";
            if (filtrados.length === 0) {
                lista.innerHTML = '<li class="data-item">Nenhum atendimento para essa data.</li>';
                return;
            }

            filtrados.forEach(a => {
                const li = document.createElement("li");
                li.className = "data-item highlighted";

                let hora = "N/A";
                if (a.ini_dataAtendimento) {
                    try {
                        hora = a.ini_dataAtendimento.split('T')[1]?.substring(0, 5) || "N/A";
                    } catch (e) { /* ignora */ }
                }

                // O back-end já devolve "tipoAtendimento" como texto legível
                // (ex: "Consulta"), então usamos direto em vez de mapear IDs.
                li.innerHTML = `
                    <div class="item-info">
                        <strong>${hora} - ${a.nomeAnimal || 'Animal ' + (a.nroAnimal || '?')}</strong>
                        <span>Tipo: ${a.tipoAtendimento || 'N/A'}</span>
                        ${a.observacoes ? `<span>Obs: ${a.observacoes}</span>` : ''}
                    </div>
                    <div class="item-actions">
                        <span class="badge waiting">Agendado</span>
                    </div>
                `;
                lista.appendChild(li);
            });
        } catch (err) {
            lista.innerHTML = '<li class="data-item">Erro ao carregar agenda.</li>';
            mostrarErroApi(err);
        }
    }

    inputData.addEventListener("change", carregar);
    const hoje = new Date().toISOString().split('T')[0];
    inputData.value = hoje;
    carregar();
}