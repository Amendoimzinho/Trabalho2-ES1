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

        const dados = {
            nomeCliente: form.nomeCliente.value.trim(),
            CPF: form.cpf.value.trim(),
            telefones: form.telefone.value.trim() ? [form.telefone.value.trim()] : [],
            logradouro: form.endereco.value.trim() || ""
        };

        try {
            const criado = await api.post("/clientes", dados);
            alert(`✅ Cliente criado com sucesso! ID: ${criado.nroCliente || 'N/A'}`);
            form.reset();
            // 🔥 CORRIGIDO: verifica se a função existe antes de chamar
            if (typeof window.carregarClientes === 'function') {
                window.carregarClientes();
            }
        } catch (err) {
            mostrarErroApi(err);
        }
    });
}

function configurarConsultaClientes() {
    const body = corpoPainel();
    const inputBusca = document.getElementById("filtro-cliente-texto");
    const lista = document.getElementById("lista-clientes");

    // 🔥 DEFINE A FUNÇÃO PRIMEIRO
    window.carregarClientes = async function() {
        lista.innerHTML = '<li class="data-item">Carregando...</li>';
        try {
            const clientes = await api.get("/clientes", { 
                nomeCliente: inputBusca.value.trim() || undefined 
            });
            renderizarLista(lista, clientes, c => ({
                titulo: c.nomeCliente ?? "(sem nome)",
                subtitulo: `CPF: ${c.CPF || 'N/A'} | Telefones: ${(c.telefones || []).join(', ') || 'N/A'}`
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

    // 🔥 AGORA CHAMA A FUNÇÃO QUE JÁ FOI DEFINIDA
    window.carregarClientes();
}

// ==========================================================================
// ANIMAIS (antigo Pets)
// ==========================================================================

function configurarCadastroAnimal() {
    const form = corpoPainel().querySelector("form");
    const btnSalvar = form.querySelector(".btn-primary");

    btnSalvar.addEventListener("click", async () => {
        if (!form.reportValidity()) return;

        const dados = {
            nome: form.nomeAnimal.value.trim(),
            nomeDono: form.nomeTutor.value.trim(),
            tipoAnimal: form.especie.value || "",
            especie: form.raca.value.trim() || "",
            genero: "",
            peso: 0,
            observacoes: ""
        };

        try {
            const tutores = await api.get("/clientes", { nomeCliente: dados.nomeDono });
            if (tutores && tutores.length > 0) {
                // OK
            } else {
                alert(`⚠️ Não encontrei nenhum cliente chamado "${dados.nomeDono}". Cadastre o cliente primeiro.`);
                return;
            }
        } catch (err) {
            mostrarErroApi(err);
            return;
        }

        try {
            await api.post("/animais", dados);
            alert(`✅ Animal cadastrado com sucesso!`);
            form.reset();
            if (typeof window.carregarAnimais === 'function') {
                window.carregarAnimais();
            }
        } catch (err) {
            if (err.message.includes("404")) {
                alert(`⚠️ O endpoint /animais ainda não existe no back-end.\n\nPara cadastrar um animal, crie um atendimento associado a ele.`);
            } else {
                mostrarErroApi(err);
            }
        }
    });
}

function configurarConsultaAnimais() {
    const inputBusca = document.getElementById("filtro-pet-texto");
    const lista = document.getElementById("lista-pets");

    window.carregarAnimais = async function() {
        lista.innerHTML = '<li class="data-item">Carregando...</li>';
        try {
            let animais = await api.get("/animais", { 
                nome: inputBusca.value.trim() || undefined 
            });
            
            if (!animais || animais.length === 0) {
                const atendimentos = await api.get("/atendimentos");
                const animaisMap = new Map();
                if (atendimentos) {
                    atendimentos.forEach(a => {
                        if (a.nroAnimal && !animaisMap.has(a.nroAnimal)) {
                            animaisMap.set(a.nroAnimal, {
                                nroAnimal: a.nroAnimal,
                                nome: a.nomeAnimal || 'Animal ' + a.nroAnimal,
                                tipoAnimal: 'N/A',
                                especie: 'N/A'
                            });
                        }
                    });
                    animais = Array.from(animaisMap.values());
                }
            }
            
            renderizarLista(lista, animais, a => ({
                titulo: a.nome ?? a.nomeAnimal ?? "(sem nome)",
                subtitulo: `Tipo: ${a.tipoAnimal || a.especie || 'N/A'}`
            }));
        } catch (err) {
            if (err.message.includes("404")) {
                lista.innerHTML = '<li class="data-item">⚠️ Endpoint /animais não disponível. Cadastre animais via atendimentos.</li>';
            } else {
                lista.innerHTML = '<li class="data-item">Erro ao carregar animais.</li>';
                mostrarErroApi(err);
            }
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

        let nroAnimal = null;
        try {
            const animais = await api.get("/animais", { 
                nome: form.nomeAnimal.value.trim() 
            });
            if (animais && animais.length > 0) {
                nroAnimal = animais[0].nroAnimal;
            } else {
                const atendimentos = await api.get("/atendimentos");
                if (atendimentos) {
                    const encontrado = atendimentos.find(a => a.nomeAnimal === form.nomeAnimal.value.trim());
                    if (encontrado) nroAnimal = encontrado.nroAnimal;
                }
            }
            
            if (!nroAnimal) {
                alert(`⚠️ Não encontrei nenhum animal chamado "${form.nomeAnimal.value.trim()}". Cadastre o animal primeiro.`);
                return;
            }
        } catch (err) {
            mostrarErroApi(err);
            return;
        }

        const dados = {
            nroTipoAtendimento: form.motivo.value === "Consulta de Rotina" ? 1 : 
                               form.motivo.value === "Vacinação" ? 2 : 1,
            nroAnimal: nroAnimal,
            nroVeterinario: 1,
            ini_dataAtendimento: `${form.data.value}T${form.hora.value}:00`,
            end_dataAtendimento: `${form.data.value}T${form.hora.value}:00`,
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
                titulo: `Atendimento #${a.nroAnimal || '?'}`,
                subtitulo: `Animal: ${a.nomeAnimal || 'N/A'} | Veterinário: ${a.nomeVeterinario || 'N/A'} | Data: ${a.ini_dataAtendimento || 'N/A'}`
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

function configurarConsultaVeterinarios() {
    const inputBusca = document.getElementById("filtro-veterinario-texto");
    const lista = document.getElementById("lista-veterinarios");

    window.carregarVeterinarios = async function() {
        lista.innerHTML = '<li class="data-item">Carregando...</li>';
        try {
            const veterinarios = await api.get("/veterinarios", { 
                nomeVeterinario: inputBusca.value.trim() || undefined 
            });
            lista.innerHTML = "";

            if (!veterinarios || veterinarios.length === 0) {
                lista.innerHTML = '<li class="data-item">Nenhum veterinário encontrado.</li>';
                return;
            }

            for (const v of veterinarios) {
                const li = document.createElement("li");
                li.className = "data-item";
                li.innerHTML = `
                    <div class="item-info">
                        <strong>${v.nome ?? "(sem nome)"}</strong>
                        <span>CRMV: ${v.CRMV || 'N/A'} | Telefone: ${v.telefone || 'N/A'}</span>
                        <span class="horarios-veterinario">Carregando horários...</span>
                    </div>
                `;
                lista.appendChild(li);

                const nroVeterinario = v.nroVeterinario;
                if (nroVeterinario !== undefined) {
                    api.get(`/veterinarios/${nroVeterinario}/horarios-disponiveis`)
                        .then(horarios => {
                            const span = li.querySelector(".horarios-veterinario");
                            if (horarios && horarios.length) {
                                const horariosStr = horarios.map(h => {
                                    if (typeof h === 'string') return h;
                                    if (h instanceof Date) return h.toLocaleString();
                                    return JSON.stringify(h);
                                }).join(' | ');
                                span.textContent = `Horários: ${horariosStr}`;
                            } else {
                                span.textContent = "Sem horários disponíveis no momento.";
                            }
                        })
                        .catch(() => {
                            li.querySelector(".horarios-veterinario").textContent = "Não foi possível carregar os horários.";
                        });
                }
            }
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