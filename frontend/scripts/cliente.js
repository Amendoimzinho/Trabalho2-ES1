// scripts/cliente.js

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

            if (templateId === "tmpl-self-agendamento") {
                configurarAgendamento();
            }
        });
    });

    const closePanel = () => panel.classList.remove("active-panel");
    document.getElementById("close-panel")?.addEventListener("click", closePanel);
    panel.addEventListener("click", (e) => {
        if (e.target.closest(".close-panel-action")) closePanel();
    });
});

// ==========================================================================
// LIGAÇÃO COM A API
// ==========================================================================
async function configurarAgendamento() {
    const form = document.getElementById("panel-dynamic-body").querySelector("form");
    const selectPet = document.getElementById("select-pet-agendamento");
    const btnSalvar = form.querySelector(".btn-primary");

    // Carrega os animais disponíveis
    try {
        let animais = await api.get("/animais");
        if (!animais || animais.length === 0) {
            // Fallback: busca dos atendimentos
            const atendimentos = await api.get("/atendimentos");
            const animaisMap = new Map();
            if (atendimentos) {
                atendimentos.forEach(a => {
                    if (a.nroAnimal && !animaisMap.has(a.nroAnimal)) {
                        animaisMap.set(a.nroAnimal, {
                            nroAnimal: a.nroAnimal,
                            nome: a.nomeAnimal || 'Animal ' + a.nroAnimal
                        });
                    }
                });
                animais = Array.from(animaisMap.values());
            }
        }

        selectPet.innerHTML = "";
        if (!animais || animais.length === 0) {
            selectPet.innerHTML = '<option value="" disabled selected>Nenhum animal cadastrado ainda</option>';
        } else {
            selectPet.innerHTML = '<option value="" disabled selected>Selecione o Animal...</option>';
            animais.forEach(a => {
                const opt = document.createElement("option");
                opt.value = a.nome ?? a.nomeAnimal ?? "";
                opt.dataset.nroAnimal = a.nroAnimal ?? "";
                opt.textContent = `${a.nome ?? a.nomeAnimal ?? '?'}${a.tipoAnimal ? " (" + a.tipoAnimal + ")" : ""}`;
                selectPet.appendChild(opt);
            });
        }
    } catch (err) {
        selectPet.innerHTML = '<option value="" disabled selected>Erro ao carregar animais</option>';
        mostrarErroApi(err);
    }

    btnSalvar.addEventListener("click", async () => {
        if (!form.reportValidity()) return;

        const opcaoSelecionada = selectPet.options[selectPet.selectedIndex];
        
        // Mapeia motivo para nroTipoAtendimento
        const motivoMap = {
            "Consulta Geral": 1,
            "Vacina": 2,
            "Banho & Tosa": 1
        };

        const dados = {
            nroTipoAtendimento: motivoMap[form.motivo.value] || 1,
            nroAnimal: parseInt(opcaoSelecionada.dataset.nroAnimal) || 0,
            nroVeterinario: 1, // Placeholder
            ini_dataAtendimento: `${form.data.value}T${form.hora.value}:00`,
            end_dataAtendimento: `${form.data.value}T${form.hora.value}:00`,
            observacoes: form.motivo.value || ""
        };

        if (!dados.nroAnimal) {
            alert("Selecione um animal válido!");
            return;
        }

        try {
            await api.post("/atendimentos", dados);
            alert("✅ Atendimento agendado com sucesso!");
            form.reset();
            selectPet.innerHTML = '<option value="" disabled selected>Carregando...</option>';
            configurarAgendamento(); // Recarrega a lista
        } catch (err) {
            mostrarErroApi(err);
        }
    });
}