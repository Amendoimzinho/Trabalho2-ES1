// scripts/gestor.js

document.addEventListener("DOMContentLoaded", () => {
    const actionButtons = document.querySelectorAll(".direct-action");
    const panel = document.getElementById("dynamic-panel");
    const panelTitle = document.getElementById("panel-title");
    const panelDynamicBody = document.getElementById("panel-dynamic-body");

    // Lógica para abrir os painéis
    actionButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const title = btn.getAttribute("data-title");
            const templateId = btn.getAttribute("data-template");

            panelTitle.innerText = title;
            panelDynamicBody.innerHTML = ""; 

            const template = document.getElementById(templateId);
            if (template) {
                panelDynamicBody.appendChild(template.content.cloneNode(true));
            }

            panel.classList.add("active-panel");
            setTimeout(() => panel.scrollIntoView({ behavior: 'smooth', block: 'start' }), 50);

            // SE o painel aberto for o de relatórios, ativa a lógica inteligente dele
            if (templateId === "tmpl-relatorios") {
                setupReportLogic();
            }
        });
    });

    // Lógica para fechar o painel
    const closePanel = () => panel.classList.remove("active-panel");
    document.getElementById("close-panel")?.addEventListener("click", closePanel);
    panel.addEventListener("click", (e) => {
        if (e.target.closest(".close-panel-action")) closePanel();
    });

    // ==========================================================================
    // SIMULADOR DO GERADOR DE RELATÓRIOS
    // ==========================================================================
    function setupReportLogic() {
        const btnGerar = document.getElementById("btn-gerar-relatorio");
        const selectType = document.getElementById("report-type");
        const resultsList = document.getElementById("report-results");

        if (!btnGerar || !selectType || !resultsList) return;

        // Banco de dados simulado para os relatórios
        const mockData = {
            pets: [
                { titulo: "Cachorro - SRD (Vira-lata)", métrica: "145 atendimentos" },
                { titulo: "Cachorro - Shih Tzu", métrica: "82 atendimentos" },
                { titulo: "Gato - SRD", métrica: "76 atendimentos" }
            ],
            motivos: [
                { titulo: "Vacinação de Rotina", métrica: "120 consultas" },
                { titulo: "Problemas Dermatológicos", métrica: "65 consultas" },
                { titulo: "Check-up Anual", métrica: "40 consultas" }
            ],
            vacinas: [
                { titulo: "V10 (Quíntupla Canina)", métrica: "98 doses" },
                { titulo: "Antirrábica", métrica: "85 doses" },
                { titulo: "V4 (Quádrupla Felina)", métrica: "42 doses" }
            ]
        };

        // Quando o gestor clica em "Gerar"
        btnGerar.addEventListener("click", () => {
            const selectedReport = selectType.value;
            const dataToShow = mockData[selectedReport];

            resultsList.innerHTML = ""; // Limpa a mensagem padrão de "Selecione..."

            // Injeta as linhas de resultado do relatório escolhido
            dataToShow.forEach(item => {
                resultsList.innerHTML += `
                    <li class="data-item">
                        <div class="item-info">
                            <strong>${item.titulo}</strong>
                        </div>
                        <div class="item-actions">
                            <span style="background-color: var(--bg-item-hover); color: var(--text-main); font-weight: 600; padding: 6px 12px; border-radius: var(--radius-buttons-sm);">
                                ${item.métrica}
                            </span>
                        </div>
                    </li>
                `;
            });
        });
    }
});