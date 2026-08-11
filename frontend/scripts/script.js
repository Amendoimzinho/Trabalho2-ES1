// ==========================================================================
// BANCO DE DADOS DAS OPÇÕES (Mapeando os Templates)
// ==========================================================================
const database = {
    clientes: [
        { nome: "Cadastrar Cliente", templateId: "tmpl-cadastrar-cliente" },
        { nome: "Consultar Clientes", templateId: "tmpl-consultar-clientes" }
    ],
    pets: [
        { nome: "Cadastrar Pet", templateId: "tmpl-cadastrar-pet" },
        { nome: "Consultar Pets", templateId: "tmpl-consultar-pets" }
    ],
    atendimentos: [
        { nome: "Agendar Atendimento", templateId: "tmpl-agendar-atendimento" },
        { nome: "Consultar Atendimentos", templateId: "tmpl-consultar-atendimentos" }
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

    // 1. Geração dos Botões ao clicar na Categoria
    categoryButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            // Controle visual do botão ativo
            categoryButtons.forEach(b => b.classList.remove("active-category"));
            btn.classList.add("active-category");

            // Busca os dados da categoria clicada
            const categoryKey = btn.getAttribute("data-category");
            const optionsData = database[categoryKey];

            // Ao trocar de categoria, fecha o painel caso estivesse aberto
            closePanel();
            optionsContainer.innerHTML = ""; // Limpa os botões anteriores

            // Cria as sub-opções dinamicamente
            optionsData.forEach(item => {
                const optionBtn = document.createElement("button");
                optionBtn.className = "btn-option";
                optionBtn.innerText = item.nome;

                // Evento para abrir o PAINEL com o template correto
                optionBtn.addEventListener("click", () => {
                    panelTitle.innerText = item.nome;
                    panelDynamicBody.innerHTML = ""; // Limpa o conteúdo anterior
                    
                    // Busca o template correspondente no HTML
                    const template = document.getElementById(item.templateId);
                    if (template) {
                        const clone = template.content.cloneNode(true);
                        panelDynamicBody.appendChild(clone);
                    }

                    // Mostra o painel adicionando a classe ativa
                    panel.classList.add("active-panel");
                    
                    // Rola a tela suavemente para o painel aparecer bem no centro
                    setTimeout(() => {
                        panel.scrollIntoView({ behavior: 'smooth', block: 'start' });
                    }, 50);
                });

                optionsContainer.appendChild(optionBtn);
            });
        });
    });

    // 2. Lógica para Fechar o Painel
    const closePanel = () => {
        panel.classList.remove("active-panel");
    };

    // Fechar pelo "X" do cabeçalho do painel
    document.getElementById("close-panel")?.addEventListener("click", closePanel);

    // Fechar clicando nos botões "Salvar/Cancelar/Fechar" dentro dos templates
    // Usamos delegação de eventos para pegar os cliques nos botões que foram injetados depois
    panel.addEventListener("click", (e) => {
        if (e.target.closest(".close-panel-action")) {
            closePanel();
        }
    });
});