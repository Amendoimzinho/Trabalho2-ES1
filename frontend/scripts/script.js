// scripts/script.js  (área do Atendente)

// ==========================================================================
// BANCO DE DADOS DAS OPÇÕES (Mapeando os Templates)
// ==========================================================================
const database = {
    clientes: [
        { nome: "Cadastrar Cliente", templateId: "tmpl-cadastrar-cliente" },
        { nome: "Consultar Clientes", templateId: "tmpl-consultar-clientes" }
    ],
    pets: [
        { nome: "Cadastrar Animal", templateId: "tmpl-cadastrar-pet" },
        { nome: "Consultar Animais", templateId: "tmpl-consultar-pets" }
    ],
    atendimentos: [
        { nome: "Agendar Atendimento", templateId: "tmpl-agendar-atendimento" },
        { nome: "Consultar Atendimentos", templateId: "tmpl-consultar-atendimentos" }
    ],
    veterinarios: [
        { nome: "Consultar Veterinários", templateId: "tmpl-consultar-veterinarios" }
    ]
};

// ==========================================================================
// LÓGICA DO SISTEMA (Painel Embutido)
// ==========================================================================
document.addEventListener("DOMContentLoaded", () => {
    const categoryButtons = document.querySelectorAll(".btn-category");
    const optionsContainer = document.getElementById("options-container");

    const panel = document.getElementById("dynamic-panel");
    const panelTitle = document.getElementById("panel-title");
    const panelDynamicBody = document.getElementById("panel-dynamic-body");

    categoryButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            categoryButtons.forEach(b => b.classList.remove("active-category"));
            btn.classList.add("active-category");

            const categoryKey = btn.getAttribute("data-category");
            const optionsData = database[categoryKey];

            closePanel();
            optionsContainer.innerHTML = "";

            optionsData.forEach(item => {
                const optionBtn = document.createElement("button");
                optionBtn.className = "btn-option";
                optionBtn.innerText = item.nome;

                optionBtn.addEventListener("click", () => {
                    panelTitle.innerText = item.nome;
                    panelDynamicBody.innerHTML = "";

                    const template = document.getElementById(item.templateId);
                    if (template) {
                        const clone = template.content.cloneNode(true);
                        panelDynamicBody.appendChild(clone);
                    }

                    panel.classList.add("active-panel");
                    setTimeout(() => panel.scrollIntoView({ behavior: 'smooth', block: 'start' }), 50);

                    configurarPainelDinamico(item.templateId);
                });

                optionsContainer.appendChild(optionBtn);
            });
        });
    });

    const closePanel = () => panel.classList.remove("active-panel");
    document.getElementById("close-panel")?.addEventListener("click", closePanel);
    panel.addEventListener("click", (e) => {
        if (e.target.closest(".close-panel-action")) closePanel();
    });
});

// ==========================================================================
// LIGAÇÃO COM A API - despachante
// ==========================================================================
function configurarPainelDinamico(templateId) {
    switch (templateId) {
        case "tmpl-cadastrar-cliente": configurarCadastroCliente(); break;
        case "tmpl-consultar-clientes": configurarConsultaClientes(); break;
        case "tmpl-cadastrar-pet": configurarCadastroAnimal(); break;
        case "tmpl-consultar-pets": configurarConsultaAnimais(); break;
        case "tmpl-agendar-atendimento": configurarAgendarAtendimento(); break;
        case "tmpl-consultar-atendimentos": configurarConsultaAtendimentos(); break;
        case "tmpl-consultar-veterinarios": configurarConsultaVeterinarios(); break;
    }
}

function corpoPainel() {
    return document.getElementById("panel-dynamic-body");
}

// ==========================================================================
// CLIENTES
// ==========================================================================

function configurarCadastroCliente() {
    const form = corpoPainel().querySelector("form");
    const btnSalvar = form.querySelector(".btn-primary");

    btnSalvar.addEventListener("click", async () => {
        if (!form.reportValidity()) return;

        // O VO Cliente do back-end não tem campo de telefone, só "emails"
        // (lista). O formulário tem um campo de telefone que, por enquanto,
        // não é persistido em lugar nenhum — mantemos o input na tela
        // (o atendente pode anotar à parte) mas não enviamos pro back-end.
        const emailDigitado = form.email.value.trim();

        const dados = {
            nomeCliente: form.nomeCliente.value.trim(),
            CPF: form.cpf.value.trim(),
            emails: emailDigitado ? [emailDigitado] : [],
            CEP: form.cep.value.trim() || null,
            logradouro: form.endereco?.value?.trim() || form.logradouro?.value?.trim() || "",
            bairro: form.bairro.value.trim() || null,
            cidade: form.cidade.value.trim() || null,
            estado: form.estado.value.trim() || null
        };

        try {
            const criado = await api.post("/clientes", dados);
            alert(`✅ Cliente criado com sucesso! ID: ${criado.nroCliente ?? 'N/A'}`);
            form.reset();
            if (typeof window.carregarClientes === 'function') {
                window.carregarClientes();
            }
        } catch (err) {
            mostrarErroApi(err);
        }
    });
}

function configurarConsultaClientes() {
    const inputBusca = document.getElementById("filtro-cliente-texto");
    const lista = document.getElementById("lista-clientes");

    window.carregarClientes = async function() {
        lista.innerHTML = '<li class="data-item">Carregando...</li>';
        try {
            const clientes = await api.get("/clientes", {
                nomeCliente: inputBusca.value.trim() || undefined
            });
            renderizarLista(lista, clientes, c => ({
                titulo: c.nomeCliente ?? "(sem nome)",
                subtitulo: `CPF: ${c.CPF || 'N/A'} | Email: ${(c.emails || []).join(', ') || 'N/A'}`
            }));
        } catch (err) {
            lista.innerHTML = '<li class="data-item">Erro ao carregar clientes.</li>';
            mostrarErroApi(err);
        }
    };

    let timer;
    inputBusca.addEventListener("input", () => {
        clearTimeout(timer);
        timer = setTimeout(window.carregarClientes, 400);
    });

    window.carregarClientes();
}

// ==========================================================================
// ANIMAIS
// ==========================================================================
// LIMITAÇÃO DO BACKEND: não existe nenhum endpoint /animais (nem GET nem
// POST). Não há como cadastrar um animal novo diretamente ainda, e a
// listagem só consegue mostrar animais que já apareceram em algum
// atendimento cadastrado (via GET /atendimentos).

function configurarCadastroAnimal() {
    const form = corpoPainel().querySelector("form");
    const btnSalvar = form.querySelector(".btn-primary");

    btnSalvar.addEventListener("click", () => {
        if (!form.reportValidity()) return;

        alert("⚠️ O back-end ainda não tem um endpoint para cadastrar animais diretamente. Por enquanto, um animal só passa a existir no sistema quando um atendimento é registrado para ele.");
    });
}

function configurarConsultaAnimais() {
    const inputBusca = document.getElementById("filtro-pet-texto");
    const lista = document.getElementById("lista-pets");

    window.carregarAnimais = async function() {
        lista.innerHTML = '<li class="data-item">Carregando...</li>';
        try {
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
            const filtro = inputBusca.value.trim().toLowerCase();
            if (filtro) {
                animais = animais.filter(a => a.nome.toLowerCase().includes(filtro));
            }

            renderizarLista(lista, animais, a => ({
                titulo: a.nome,
                subtitulo: `ID: ${a.nroAnimal}`
            }));
        } catch (err) {
            lista.innerHTML = '<li class="data-item">Erro ao carregar animais.</li>';
            mostrarErroApi(err);
        }
    };

    let timer;
    inputBusca.addEventListener("input", () => {
        clearTimeout(timer);
        timer = setTimeout(window.carregarAnimais, 400);
    });

    window.carregarAnimais();
}

// ==========================================================================
// ATENDIMENTOS
// ==========================================================================

function configurarAgendarAtendimento() {
    const form = corpoPainel().querySelector("form");
    const btnSalvar = form.querySelector(".btn-primary");

    btnSalvar.addEventListener("click", async () => {
        if (!form.reportValidity()) return;

        // LIMITAÇÃO DO BACKEND: sem /animais, só conseguimos achar o
        // nroAnimal de um animal que já teve atendimento antes.
        let nroAnimal = null;
        try {
            const atendimentos = await api.get("/atendimentos");
            const nomeDigitado = form.nomeAnimal.value.trim();
            const encontrado = (atendimentos || []).find(a => a.nomeAnimal === nomeDigitado);
            if (encontrado) nroAnimal = encontrado.nroAnimal;

            if (!nroAnimal) {
                alert(`⚠️ Não encontrei nenhum animal chamado "${nomeDigitado}" com atendimentos anteriores. Ainda não é possível cadastrar um animal novo pelo sistema.`);
                return;
            }
        } catch (err) {
            mostrarErroApi(err);
            return;
        }

        // LIMITAÇÃO DO BACKEND: o formulário não tem campo de veterinário.
        // Usamos o primeiro veterinário cadastrado como padrão até o
        // formulário ganhar um seletor de verdade.
        let nroVeterinario = null;
        try {
            const veterinarios = await api.get("/veterinarios");
            if (veterinarios && veterinarios.length > 0) {
                nroVeterinario = veterinarios[0].nroVeterinario;
            }
        } catch (err) {
            mostrarErroApi(err);
            return;
        }

        if (!nroVeterinario) {
            alert("⚠️ Não há nenhum veterinário cadastrado no sistema.");
            return;
        }

        // Observação: o back-end sempre grava o atendimento com o mesmo
        // tipo (o primeiro cadastrado no banco) — não existe campo de tipo
        // no POST. O "motivo" escolhido no formulário vira só a observação.
        const dados = {
            nroAnimal: nroAnimal,
            nroVeterinario: nroVeterinario,
            ini_dataAtendimento: `${form.data.value}T${form.hora.value}:00`,
            observacoes: form.motivo.value || ""
        };

        try {
            await api.post("/atendimentos", dados);
            alert(`✅ Atendimento agendado com sucesso!`);
            form.reset();
            if (typeof window.carregarAtendimentos === 'function') {
                window.carregarAtendimentos();
            }
        } catch (err) {
            mostrarErroApi(err);
        }
    });
}

function configurarConsultaAtendimentos() {
    const inputBusca = document.getElementById("filtro-atendimento-cliente");
    const lista = document.getElementById("lista-atendimentos");

    window.carregarAtendimentos = async function() {
        lista.innerHTML = '<li class="data-item">Carregando...</li>';
        try {
            const atendimentos = await api.get("/atendimentos", {
                nomeCliente: inputBusca.value.trim() || undefined
            });
            renderizarLista(lista, atendimentos, a => ({
                titulo: a.nomeAnimal || `Animal ${a.nroAnimal || '?'}`,
                subtitulo: `Veterinário: ${a.nomeVeterinario || 'N/A'} | Tipo: ${a.tipoAtendimento || 'N/A'} | Data: ${a.ini_dataAtendimento || 'N/A'}`
            }));
        } catch (err) {
            lista.innerHTML = '<li class="data-item">Erro ao carregar atendimentos.</li>';
            mostrarErroApi(err);
        }
    };

    let timer;
    inputBusca.addEventListener("input", () => {
        clearTimeout(timer);
        timer = setTimeout(window.carregarAtendimentos, 400);
    });

    window.carregarAtendimentos();
}

// ==========================================================================
// VETERINÁRIOS
// ==========================================================================
// LIMITAÇÃO DO BACKEND: o ControllerVeterinario só tem listagem por nome/id.
// Não existe endpoint de horários disponíveis exposto via HTTP (a lógica
// existe em ServiceVeterinario.calcularHorariosDisponiveis, mas ninguém
// chama ela publicamente ainda), nem CRMV/telefone no VO. Por isso a lista
// abaixo mostra só nome e id.

function configurarConsultaVeterinarios() {
    const inputBusca = document.getElementById("filtro-veterinario-texto");
    const lista = document.getElementById("lista-veterinarios");

    window.carregarVeterinarios = async function() {
        lista.innerHTML = '<li class="data-item">Carregando...</li>';
        try {
            const veterinarios = await api.get("/veterinarios", {
                nomeVeterinario: inputBusca.value.trim() || undefined
            });
            renderizarLista(lista, veterinarios, v => ({
                titulo: v.nomeVeterinario ?? "(sem nome)",
                subtitulo: `ID: ${v.nroVeterinario ?? 'N/A'}`
            }));
        } catch (err) {
            lista.innerHTML = '<li class="data-item">Erro ao carregar veterinários.</li>';
            mostrarErroApi(err);
        }
    };

    let timer;
    inputBusca.addEventListener("input", () => {
        clearTimeout(timer);
        timer = setTimeout(window.carregarVeterinarios, 400);
    });

    window.carregarVeterinarios();
}

// ==========================================================================
// UTIL DE RENDERIZAÇÃO
// ==========================================================================

function renderizarLista(ul, itens, mapear) {
    ul.innerHTML = "";
    if (!itens || itens.length === 0) {
        ul.innerHTML = '<li class="data-item">Nenhum resultado encontrado.</li>';
        return;
    }
    itens.forEach(item => {
        const { titulo, subtitulo } = mapear(item);
        const li = document.createElement("li");
        li.className = "data-item";
        li.innerHTML = `
            <div class="item-info">
                <strong>${titulo}</strong>
                <span>${subtitulo || ""}</span>
            </div>
            <div class="item-actions">
                <button class="action-btn" title="Visualizar dados brutos">
                    <span class="material-symbols-outlined">visibility</span>
                </button>
            </div>
        `;
        li.querySelector(".action-btn").addEventListener("click", () => alert(JSON.stringify(item, null, 2)));
        ul.appendChild(li);
    });
}