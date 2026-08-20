document.addEventListener('DOMContentLoaded', async () => {
    // navegacao
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
        });
    });

    // veterinario dropdown list no agendar
    const selectVet = document.getElementById('select-vet-agendamento');
    try {
        const veterinarios = await api.listarVeterinarios();
        if (selectVet && Array.isArray(veterinarios)) {
            veterinarios.forEach(v => {
                const opt = document.createElement('option');

                opt.value = v.nroVeterinario;

                opt.textContent = `Dr(a). ${v.nome} (CRMV: ${v.CRMV || '-'})`;
                selectVet.appendChild(opt);
            });
        }
    } catch (err) {
        console.warn('Não foi possível carregar veterinários automaticamente:', err);
    }

    // bucar qual tutor "logou"
    let nomeClienteLogado = null;

    const btnBuscar = document.getElementById('btn-buscar-tutor');
    const inputCpf = document.getElementById('input-cpf-filtro');
    const gridPets = document.getElementById('grid-pets');

    btnBuscar.addEventListener('click', async () => {
        const termo = inputCpf.value.replace(/\D/g, ''); 
        
        if (!termo) {
            alert('Por favor, informe seu CPF ou ID.');
            return;
        }
        
        gridPets.innerHTML = '<p>Buscando dados...</p>';
        document.querySelector('#tabela-historico-cliente tbody').innerHTML = '<tr><td colspan="5">Atualizando...</td></tr>';

        try {
            let cliente = null;

            if (termo.length === 11) {
                cliente = await api.buscarClientePorCpf(termo);
            } else {
                const lista = await api.buscarClientePorId(termo);
                cliente = (lista && lista.length > 0) ? lista[0] : null;
            }

            if (!cliente) {
                gridPets.innerHTML = '<p style="color: var(--danger);">Cliente não encontrado.</p>';
                return;
            }

            nomeClienteLogado = cliente.nomeCliente;
            
            // Renderizar Pets
            // SERVICO NAO EXISTE AINDA, VAI MOSTRAR MENSAGEM DE ERRO!!!

            // const pets = [];
            // if (pets.length === 0) {
            //     gridPets.innerHTML = '<p style="color: var(--muted-text);">Você ainda não tem animais cadastrados.</p>';
            // } else {
            //     gridPets.innerHTML = pets.map(p => `
            //     <div style="background: var(--light-blue); padding: 1.2rem; border-radius: 10px; border-left: 4px solid var(--secondary-blue);">
            //         <h3 style="color: var(--primary-blue); margin-bottom: 8px;">🐾 ${p.nome || p.nomeAnimal}</h3>
            //         <p style="font-size: 0.9rem; color: var(--dark-text);"><strong>Idade:</strong> ${p.idade || '-'} anos</p>
            //         <p style="font-size: 0.9rem; color: var(--dark-text);"><strong>Peso:</strong> ${p.peso ? p.peso + ' kg' : '-'}</p>
            //     </div>
            //     `).join('');
            // }

            //nomeClienteLogado = cliente;
            await carregarHistoricoCliente();

            

        } catch (err) {
        console.error(err);
        gridPets.innerHTML = '<p style="color: var(--danger);">Erro ao buscar informações do tutor.</p>';
        }
    });


    const formAgendamento = document.getElementById('form-agendamento');
    if (formAgendamento) {
        formAgendamento.addEventListener('submit', async (e) => {
        e.preventDefault();

            const dataSelecionada = document.getElementById('data-agendamento').value;
            const horaSelecionada = document.getElementById('hora-agendamento').value;

            const dataHoraFormatada = `${dataSelecionada}T${horaSelecionada}:00`; 

            const novoAgendamento = {
                nroAnimal: parseInt(document.getElementById('input-pet-agendamento').value),
                nroVeterinario: parseInt(document.getElementById('select-vet-agendamento').value),
                ini_dataAtendimento: dataHoraFormatada,
                observacoes: document.getElementById('motivo-consulta').value,
                nroTipoAtendimento: 1 
            };

            const btnSubmit = formAgendamento.querySelector('button[type="submit"]');
            btnSubmit.innerText = 'Agendando...';
            btnSubmit.disabled = true;

            try {
                await api.criarAtendimento(novoAgendamento);
                alert('Agendamento realizado com sucesso!');
                formAgendamento.reset();
                
                await carregarHistoricoCliente(); 
                
                const btnHistorico = document.querySelector('[data-target="secao-historico"]');
                if (btnHistorico) btnHistorico.click();
                
            } catch (err) {
                alert('Erro ao agendar: O servidor recusou os dados (Erro 400). Verifique o console.');
                console.error("Detalhe do erro:", err);
            } finally {
                btnSubmit.innerText = 'Confirmar Agendamento';
                btnSubmit.disabled = false; 
            }
        });
    }

    // historico de cliente
    async function carregarHistoricoCliente() {
        const tbody = document.querySelector('#tabela-historico-cliente tbody');
        if (!tbody) return;

        if (!nomeClienteLogado) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: var(--muted-text);">Identifique-se primeiro (aba Meus Pets) para ver seu histórico.</td></tr>';
            return;
        }

        try {
            // Usa o endpoint de atendimento passando o nome do cliente logado
            const atendimentos = await api.listarAtendimentos(nomeClienteLogado);
            
            if (!atendimentos || atendimentos.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: var(--muted-text);">Nenhum atendimento registrado para você.</td></tr>';
                return;
            }

            tbody.innerHTML = atendimentos.map(a => {
                let dataExibicao = '-';
                
                // converte LocalDateTime.toString()
                if (a.ini_dataAtendimento) {
                    const [dataParte, horaParte] = a.ini_dataAtendimento.split('T');
                
                    if (dataParte && horaParte) {
                        const [ano, mes, dia] = dataParte.split('-');
                        const hora = horaParte.substring(0, 5);
                        dataExibicao = `${dia}/${mes}/${ano} às ${hora}`;
                    } else {
                        dataExibicao = a.ini_dataAtendimento; 
                    }
                }

                return `
                    <tr>
                    <td>${dataExibicao}</td>
                    <td>${a.nomeAnimal || '-'}</td>
                    <td><span class="badge-tag">Consulta</span></td>
                    <td>Dr(a). ${a.nomeVeterinario || '-'}</td>
                    <td><span style="color: var(--success); font-weight: bold;">Confirmado</span></td>
                    </tr>
                `;
            }).join('');
        } catch (err) {
            console.error(err);
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: var(--danger);">Erro ao carregar histórico.</td></tr>';
        }
    }

    navButtons.forEach((btn) => {
        btn.addEventListener('click', () => {
            const targetId = btn.getAttribute('data-target');
            if (targetId === 'secao-historico') carregarHistoricoCliente();
        });
    });
});