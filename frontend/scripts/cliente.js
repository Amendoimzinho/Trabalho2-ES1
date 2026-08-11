// scripts/cliente.js

document.addEventListener("DOMContentLoaded", () => {
    // Seleciona todos os botões de ação direta
    const actionButtons = document.querySelectorAll(".direct-action");
    
    // Seleciona os elementos do painel
    const panel = document.getElementById("dynamic-panel");
    const panelTitle = document.getElementById("panel-title");
    const panelDynamicBody = document.getElementById("panel-dynamic-body");

    // Lógica ao clicar em qualquer botão de ação
    actionButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            // Pega as informações armazenadas no próprio botão
            const title = btn.getAttribute("data-title");
            const templateId = btn.getAttribute("data-template");

            // Atualiza o título e limpa o corpo do painel
            panelTitle.innerText = title;
            panelDynamicBody.innerHTML = ""; 

            // Busca o template no HTML e injeta no painel
            const template = document.getElementById(templateId);
            if (template) {
                const clone = template.content.cloneNode(true);
                panelDynamicBody.appendChild(clone);
            }

            // Exibe o painel
            panel.classList.add("active-panel");
            
            // Rola a tela suavemente para o painel
            setTimeout(() => {
                panel.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }, 50);
        });
    });

    // Função para fechar o painel
    const closePanel = () => {
        panel.classList.remove("active-panel");
    };

    // Fechar pelo "X" do cabeçalho
    document.getElementById("close-panel")?.addEventListener("click", closePanel);

    // Fechar ao clicar nos botões de "Cancelar" ou "Confirmar" dentro dos templates
    panel.addEventListener("click", (e) => {
        if (e.target.closest(".close-panel-action")) {
            closePanel();
        }
    });
});