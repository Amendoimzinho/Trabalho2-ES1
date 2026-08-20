document.addEventListener('DOMContentLoaded', () => {
    // Navegacao
    const navButtons = document.querySelectorAll('.subnav-btn');
    const sections = document.querySelectorAll('.content-section');

    navButtons.forEach((btn) => {
        btn.addEventListener('click', () => {
            navButtons.forEach((b) => b.classList.remove('active'));
            sections.forEach((s) => s.classList.remove('active'));

            btn.classList.add('active');
            const targetId = btn.getAttribute('data-target');
            const targetSection = document.getElementById(targetId);
            if (targetSection) targetSection.classList.add('active');

            if (targetId === 'secao-clientes') carregarListaClientes();
            if (targetId === 'secao-veterinarios') carregarListaVeterinarios();
            if (targetId === 'secao-atendimentos') carregarListaAtendimentos();
        });
    });

    // Mascaras
    const inputCpf = document.getElementById('cli-cpf');
    const inputTelefone = document.getElementById('cli-telefone');
    const inputCep = document.getElementById('cli-cep');

    inputCpf?.addEventListener('input', (e) => {
        let v = e.target.value.replace(/\D/g, '');
        v = v.replace(/(\d{3})(\d)/, '$1.$2');
        v = v.replace(/(\d{3})(\d)/, '$1.$2');
        v = v.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        e.target.value = v;
    });

    inputTelefone?.addEventListener('input', (e) => {
        let v = e.target.value.replace(/\D/g, '');
        v = v.replace(/^(\d{2})(\d)/g, '($1) $2');
        v = v.replace(/(\d)(\d{4})$/, '$1-$2');
        e.target.value = v;
    });

    inputCep?.addEventListener('input', (e) => {
        let v = e.target.value.replace(/\D/g, '');
        v = v.replace(/^(\d{5})(\d)/, '$1-$2');
        e.target.value = v;
    });

    // Consultar CEP
    inputCep?.addEventListener('blur', async () => {
        const cepLimpo = inputCep.value.replace(/\D/g, '');
        if (cepLimpo.length !== 8) return;

        const campoRua = document.getElementById('cli-logradouro');
        const campoBairro = document.getElementById('cli-bairro');
        const campoCidade = document.getElementById('cli-cidade');
        const campoUf = document.getElementById('cli-uf');
        const campoIbge = document.getElementById('cli-ibge');

        if (campoRua) campoRua.value = 'Buscando endereço...';

        try {
            const res = await fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`);
            const dados = await res.json();

            if (dados.erro) {
                alert('CEP não encontrado.');
                limparCamposEndereco();
                return;
            }

            if (campoRua) campoRua.value = dados.logradouro || '';
            if (campoBairro) campoBairro.value = dados.bairro || '';
            if (campoCidade) campoCidade.value = dados.localidade || '';
            if (campoUf) campoUf.value = dados.uf || '';
            if (campoIbge) campoIbge.value = dados.ibge || '';

            document.getElementById('cli-numero')?.focus();
        } catch (err) {
            console.error(err);
            limparCamposEndereco();
        }
    });

    function limparCamposEndereco() {
        ['cli-logradouro', 'cli-bairro', 'cli-cidade', 'cli-uf', 'cli-ibge'].forEach((id) => {
            const el = document.getElementById(id);
            if (el) el.value = '';
        });
    }

    // cadastrar cliente
    const formCliente = document.getElementById('form-cadastrar-cliente');
    if (formCliente) {
        formCliente.addEventListener('submit', async (e) => {
        e.preventDefault();

        const novoCliente = {
            nomeCliente: document.getElementById('cli-nome').value.trim(),
            CPF: document.getElementById('cli-cpf').value.replace(/\D/g, ''),
            emails: [document.getElementById('cli-email').value.trim()].filter(Boolean),
            CEP: document.getElementById('cli-cep').value.replace(/\D/g, ''),
            logradouro: document.getElementById('cli-logradouro')?.value || '',
            bairro: document.getElementById('cli-bairro')?.value || '',
            cidade: document.getElementById('cli-cidade')?.value || '',
            estado: document.getElementById('cli-uf')?.value || ''
        };

        try {
            await api.criarCliente(novoCliente);
            alert('Cliente cadastrado com sucesso!');
            formCliente.reset();
            limparCamposEndereco();
            carregarListaClientes();
        } catch (err) {
            alert('Erro ao cadastrar cliente: ' + err.message);
        }
        });
    }

    // Tabela de Clientes
    async function carregarListaClientes() {
        const tbody = document.querySelector('#tabela-clientes tbody');
        if (!tbody) return;

        try {
            const clientes = await api.listarClientes();
            if (!clientes || clientes.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Nenhum cliente cadastrado.</td></tr>';
                return;
            }

            tbody.innerHTML = clientes.map((c) => `
                <tr>
                <td>#${c.nroCliente ?? '-'}</td>
                <td><strong>${c.nomeCliente || '-'}</strong></td>
                <td>${c.CPF || '-'}</td>
                <td>${c.emails?.[0] || '-'}</td>
                <td>${c.cidade ? c.cidade + ' / ' + (c.estado || '') : '-'}</td>
                </tr>
            `).join('');
        } catch (err) {
            console.error(err);
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: red;">Erro ao carregar clientes.</td></tr>';
        }
    }

    // Carregar veterinarios
    async function carregarListaVeterinarios() {
        const tbody = document.querySelector('#tabela-veterinarios tbody');
        if (!tbody) return;

        try {
            const vets = await api.listarVeterinarios();
            if (!vets || vets.length === 0) {
                tbody.innerHTML = '<tr><td colspan="3" style="text-align: center;">Nenhum veterinário cadastrado.</td></tr>';
                return;
            }

            tbody.innerHTML = vets.map((v) => `
                <tr>
                <td>#${v.nroVeterinario ?? '-'}</td>
                <td><strong>Dr(a). ${v.nome || '-'}</strong></td>
                <td><span class="badge-tag">${v.CRMV || 'Sem CRMV'}</span></td>
                </tr>
            `).join('');
        } catch (err) {
            console.error(err);
            tbody.innerHTML = '<tr><td colspan="3" style="text-align: center; color: red;">Erro ao carregar veterinários.</td></tr>';
        }
    }

    // Atendimentos
    async function carregarListaAtendimentos() {
        const tbody = document.querySelector('#tabela-atendimentos-atendente tbody');
        if (!tbody) return;

        try {
            const atendimentos = await api.listarAtendimentos();
            if (!atendimentos || atendimentos.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Nenhum atendimento agendado.</td></tr>';
                return;
            }

            tbody.innerHTML = atendimentos.map((a) => {
                let dataExibicao = '-';
                if (a.ini_dataAtendimento) {
                    const partes = a.ini_dataAtendimento.split('T');
                    if (partes.length === 2) {
                        const [ano, mes, dia] = partes[0].split('-');
                        const hora = partes[1].substring(0, 5);
                        dataExibicao = `${dia}/${mes}/${ano} às ${hora}`;
                    } else {
                        dataExibicao = a.ini_dataAtendimento;
                    }
                }

                let tipoNome = 'Consulta';
                if (a.nroTipoAtendimento === 3) tipoNome = 'Vacinação';

                return `
                    <tr>
                    <td>#${a.nroAtendimento || '-'}</td>
                    <td>${dataExibicao}</td>
                    <td>${a.nomeAnimal || '-'}</td>
                    <td>Dr(a). ${a.nomeVeterinario || '-'}</td>
                    <td><span class="badge-tag">${tipoNome}</span></td>
                    </tr>
                `;
            }).join('');
        } catch (err) {
            console.error(err);
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: red;">Erro ao carregar atendimentos.</td></tr>';
        }
    }

    const formAtendimento = document.getElementById('form-agendar-atendente');
    if (formAtendimento) {
        formAtendimento.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const btnSubmit = formAtendimento.querySelector('button[type="submit"]');
            btnSubmit.innerText = 'Agendando...';
            btnSubmit.disabled = true;

            const dataSelecionada = document.getElementById('atend-data').value;
            const horaSelecionada = document.getElementById('atend-hora').value;
            const dataHoraFormatada = `${dataSelecionada}T${horaSelecionada}:00`; 

            const novoAgendamento = {
                nroAnimal: parseInt(document.getElementById('atend-pet-id').value),
                nroVeterinario: parseInt(document.getElementById('atend-vet-id').value),
                ini_dataAtendimento: dataHoraFormatada,
                nroTipoAtendimento: parseInt(document.getElementById('atend-tipo').value)
            };

            try {
                await api.criarAtendimento(novoAgendamento);
                alert('✅ Agendamento realizado com sucesso!');
                formAtendimento.reset();
                await carregarListaAtendimentos();
            } catch (err) {
                alert('❌ Erro ao agendar: ' + err.message);
            } finally {
                btnSubmit.innerText = 'Confirmar Agendamento';
                btnSubmit.disabled = false;
            }
        });
    }

    carregarListaClientes();
});