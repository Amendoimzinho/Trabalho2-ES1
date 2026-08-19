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

    // LIMITAÇÃO DO BACKEND: não existe endpoint /animais, nem endpoint para
    // criar um animal novo. A única forma de "descobrir" animais existentes
    // é olhando os atendimentos já cadastrados (GET /atendimentos) e
    // extraindo o par nroAnimal/nomeAnimal de lá. Ou seja: só aparecem aqui
    // animais que já tiveram pelo menos um atendimento registrado no banco.
    let animaisDisponiveis = [];
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
        animaisDisponiveis = Array.from(animaisMap.values());

        selectPet.innerHTML = "";
        if (animaisDisponiveis.length === 0) {
            selectPet.innerHTML = '<option value="" disabled selected>Nenhum animal encontrado (fale com a recepção)</option>';
        } else {
            selectPet.innerHTML = '<option value="" disabled selected>Selecione o Animal...</option>';
            animaisDisponiveis.forEach(a => {
                const opt = document.createElement("option");
                opt.value = a.nome;
                opt.dataset.nroAnimal = a.nroAnimal;
                opt.textContent = a.nome;
                selectPet.appendChild(opt);
            });
        }
    } catch (err) {
        selectPet.innerHTML = '<option value="" disabled selected>Erro ao carregar animais</option>';
        mostrarErroApi(err);
    }

    // LIMITAÇÃO DO BACKEND: o formulário não tem campo para escolher o
    // veterinário, mas o back-end exige um nroVeterinario para agendar.
    // Buscamos a lista de veterinários e usamos o primeiro disponível.
    // Quando o formulário ganhar um <select> de veterinário, troque essa
    // lógica por um valor escolhido pelo usuário.
    let nroVeterinarioPadrao = null;
    try {
        const veterinarios = await api.get("/veterinarios");
        if (veterinarios && veterinarios.length > 0) {
            nroVeterinarioPadrao = veterinarios[0].nroVeterinario;
        }
    } catch (err) {
        // Se falhar, avisamos só na hora de salvar (abaixo).
    }

    btnSalvar.addEventListener("click", async () => {
        if (!form.reportValidity()) return;

        const opcaoSelecionada = selectPet.options[selectPet.selectedIndex];
        const nroAnimal = opcaoSelecionada ? parseInt(opcaoSelecionada.dataset.nroAnimal) : NaN;

        if (!nroAnimal || Number.isNaN(nroAnimal)) {
            alert("Selecione um animal válido!");
            return;
        }

        if (!nroVeterinarioPadrao) {
            alert("Não há nenhum veterinário cadastrado no sistema. Não é possível agendar agora.");
            return;
        }

        // Observação: o back-end (AtendimentoConsulta / ServiceAtendimento)
        // não usa um campo de "tipo" no POST — o tipo de atendimento é
        // sempre fixado como o primeiro tipo cadastrado no banco. Por isso
        // não enviamos nroTipoAtendimento aqui; guardamos o motivo só como
        // observação em texto.
        const dados = {
            nroAnimal: nroAnimal,
            nroVeterinario: nroVeterinarioPadrao,
            ini_dataAtendimento: `${form.data.value}T${form.hora.value}:00`,
            observacoes: form.motivo.value || ""
        };

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