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
    // LIGAÇÃO COM A API - Cadastro de Funcionário (Veterinário)
    // ==========================================================================
    function configurarCadastroFuncionario() {
        const form = panelDynamicBody.querySelector("form");
        const btnSalvar = form.querySelector(".btn-primary");

        btnSalvar.addEventListener("click", async () => {
            if (!form.reportValidity()) return;

            const cargo = form.cargo.value;

            // Hoje o back-end só tem endpoint de cadastro para Veterinário
            if (cargo !== "Veterinário(a)") {
                alert(`⚠️ Cadastro de "${cargo}" ainda não está disponível no back-end.\nApenas Veterinário(a) pode ser cadastrado.`);
                return;
            }

            const dados = {
                nome: form.nomeVeterinario.value.trim(),
                CRMV: form.crmv.value.trim() || "",
                telefone: form.email.value.trim() || ""  // Usa email como telefone se não houver campo específico
            };

            try {
                const criado = await api.post("/veterinarios", dados);
                alert(`✅ Veterinário cadastrado com sucesso! ID: ${criado.nroVeterinario || 'N/A'}`);
                form.reset();
            } catch (err) {
                mostrarErroApi(err);
            }
        });
    }

    // ==========================================================================
    // SIMULADOR DO GERADOR DE RELATÓRIOS
    // ==========================================================================
    function setupReportLogic() {
        const btnGerar = document.getElementById("btn-gerar-relatorio");
        const selectType = document.getElementById("report-type");
        const resultsList = document.getElementById("report-results");

        if (!btnGerar || !selectType || !resultsList) return;

        // Dados mockados (porque não há endpoint de relatórios)
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
            
            // Tenta buscar dados reais se possível
            try {
                const atendimentos = await api.get("/atendimentos");
                if (atendimentos && atendimentos.length > 0) {
                    // Gera relatório real baseado nos atendimentos
                    const tipos = {};
                    atendimentos.forEach(a => {
                        const tipo = a.nroTipoAtendimento || 'desconhecido';
                        tipos[tipo] = (tipos[tipo] || 0) + 1;
                    });
                    
                    resultsList.innerHTML = "";
                    Object.entries(tipos).forEach(([tipo, count]) => {
                        const nomeTipo = tipo === 1 ? 'Consultas' : tipo === 2 ? 'Vacinações' : 'Outros';
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
                // Fallback para mock
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
                                ${item.métrica}
                            </span>
                        </div>
                    </li>
                `;
            });
        });
    }
});