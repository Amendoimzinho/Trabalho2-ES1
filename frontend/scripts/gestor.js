// scripts/gestor.js

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
                panelDynamicBody.appendChild(template.content.cloneNode(true));
            }

            panel.classList.add("active-panel");
            setTimeout(() => panel.scrollIntoView({ behavior: 'smooth', block: 'start' }), 50);

            if (templateId === "tmpl-relatorios") {
                setupReportLogic();
            }
            if (templateId === "tmpl-cadastrar-funcionario") {
                configurarCadastroFuncionario();
            }
        });
    });

    const closePanel = () => panel.classList.remove("active-panel");
    document.getElementById("close-panel")?.addEventListener("click", closePanel);
    panel.addEventListener("click", (e) => {
        if (e.target.closest(".close-panel-action")) closePanel();
    });

    // ==========================================================================
    // Cadastro de Funcionário (Veterinário)
    // ==========================================================================
    // LIMITAÇÃO DO BACKEND: não existe (ainda) um POST /veterinarios no
    // back-end — o ControllerVeterinario só tem listagem (GET). Até esse
    // endpoint ser criado, deixamos o botão de salvar avisando o usuário
    // em vez de tentar chamar uma rota que vai dar 404.
    function configurarCadastroFuncionario() {
        const form = panelDynamicBody.querySelector("form");
        const btnSalvar = form.querySelector(".btn-primary");

        btnSalvar.addEventListener("click", () => {
            if (!form.reportValidity()) return;

            alert("⚠️ O cadastro de funcionários ainda não está disponível: o back-end não tem uma rota para criar veterinários (só para listar). Peça pra alguém cadastrar direto no banco por enquanto.");
        });
    }

    // ==========================================================================
    // GERADOR DE RELATÓRIOS
    // ==========================================================================
    function setupReportLogic() {
        const btnGerar = document.getElementById("btn-gerar-relatorio");
        const selectType = document.getElementById("report-type");
        const resultsList = document.getElementById("report-results");

        if (!btnGerar || !selectType || !resultsList) return;

        // Dados mockados (porque não há endpoint dedicado de relatórios —
        // isso é só um fallback visual caso não existam atendimentos ainda)
        const mockData = {
            pets: [
                { titulo: "Cachorro - SRD (Vira-lata)", metrica: "145 atendimentos" },
                { titulo: "Cachorro - Shih Tzu", metrica: "82 atendimentos" },
                { titulo: "Gato - SRD", metrica: "76 atendimentos" }
            ],
            motivos: [
                { titulo: "Vacinação de Rotina", metrica: "120 consultas" },
                { titulo: "Problemas Dermatológicos", metrica: "65 consultas" },
                { titulo: "Check-up Anual", metrica: "40 consultas" }
            ],
            vacinas: [
                { titulo: "V10 (Quíntupla Canina)", metrica: "98 doses" },
                { titulo: "Antirrábica", metrica: "85 doses" },
                { titulo: "V4 (Quádrupla Felina)", metrica: "42 doses" }
            ]
        };

        btnGerar.addEventListener("click", async () => {
            const selectedReport = selectType.value;

            // Tenta montar um relatório real com base nos atendimentos
            try {
                const atendimentos = await api.get("/atendimentos");
                if (atendimentos && atendimentos.length > 0) {
                    // O back-end já devolve "tipoAtendimento" como texto
                    // legível (ex: "Consulta"), então agrupamos direto por
                    // esse valor em vez de tentar adivinhar um id numérico.
                    const contagemPorTipo = {};
                    atendimentos.forEach(a => {
                        const nomeTipo = a.tipoAtendimento || "Não informado";
                        contagemPorTipo[nomeTipo] = (contagemPorTipo[nomeTipo] || 0) + 1;
                    });

                    resultsList.innerHTML = "";
                    Object.entries(contagemPorTipo).forEach(([nomeTipo, count]) => {
                        resultsList.innerHTML += `
                            <li class="data-item">
                                <div class="item-info">
                                    <strong>${nomeTipo}</strong>
                                </div>
                                <div class="item-actions">
                                    <span style="background-color: var(--color-teal-light); color: var(--color-teal-dark); font-weight: 700; padding: 6px 12px; border-radius: var(--radius-lg);">
                                        ${count} atendimentos
                                    </span>
                                </div>
                            </li>
                        `;
                    });
                    return;
                }
            } catch (e) {
                // Se falhar, cai no mock abaixo
            }

            // Usa dados mockados
            const dataToShow = mockData[selectedReport] || mockData.pets;
            resultsList.innerHTML = "";
            dataToShow.forEach(item => {
                resultsList.innerHTML += `
                    <li class="data-item">
                        <div class="item-info">
                            <strong>${item.titulo}</strong>
                        </div>
                        <div class="item-actions">
                            <span style="background-color: var(--color-teal-light); color: var(--color-teal-dark); font-weight: 700; padding: 6px 12px; border-radius: var(--radius-lg);">
                                ${item.metrica}
                            </span>
                        </div>
                    </li>
                `;
            });
        });
    }
});