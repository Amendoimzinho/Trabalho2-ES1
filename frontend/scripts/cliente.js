document.addEventListener('DOMContentLoaded', async () => {
  // 1. NAVEGAÇÃO ENTRE ABAS
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

  // 2. POVOAR LISTA DE VETERINÁRIOS NO SELECT
  const selectVet = document.getElementById('select-vet-agendamento');
  try {
    const veterinarios = await api.listarVeterinarios();
    if (selectVet && Array.isArray(veterinarios)) {
      veterinarios.forEach(v => {
        const opt = document.createElement('option');
        opt.value = v.id || v.idVeterinario;
        opt.textContent = `Dr(a). ${v.nome || v.nomeVeterinario} (CRMV: ${v.crmv || '-'})`;
        selectVet.appendChild(opt);
      });
    }
  } catch (err) {
    console.warn('Não foi possível carregar veterinários automaticamente:', err);
  }

  // 3. BUSCA DE DADOS DO TUTOR E SEUS PETS
  const btnBuscar = document.getElementById('btn-buscar-tutor');
  const inputCpf = document.getElementById('input-cpf-filtro');
  const gridPets = document.getElementById('grid-pets');
  const selectPet = document.getElementById('select-pet-agendamento');

  btnBuscar.addEventListener('click', async () => {
    const cpf = inputCpf.value.trim();
    if (!cpf) {
      alert('Por favor, informe seu CPF.');
      return;
    }

    try {
      const cliente = await api.buscarClientePorCpf(cpf);
      if (!cliente) {
        gridPets.innerHTML = '<p style="color: var(--danger);">Cliente não encontrado com este CPF.</p>';
        return;
      }

      // Renderizar Pets
      const pets = cliente.animais || cliente.pets || [];
      if (pets.length === 0) {
        gridPets.innerHTML = '<p style="color: var(--muted-text);">Você ainda não tem animais cadastrados.</p>';
      } else {
        gridPets.innerHTML = pets.map(p => `
          <div style="background: var(--light-blue); padding: 1.2rem; border-radius: 10px; border-left: 4px solid var(--secondary-blue);">
            <h3 style="color: var(--primary-blue); margin-bottom: 8px;">🐾 ${p.nome || p.nomeAnimal}</h3>
            <p style="font-size: 0.9rem; color: var(--dark-text);"><strong>Idade:</strong> ${p.idade || '-'} anos</p>
            <p style="font-size: 0.9rem; color: var(--dark-text);"><strong>Peso:</strong> ${p.peso ? p.peso + ' kg' : '-'}</p>
          </div>
        `).join('');

        // Preenche o select do agendamento
        selectPet.innerHTML = '<option value="">Selecione um pet...</option>';
        pets.forEach(p => {
          const opt = document.createElement('option');
          opt.value = p.id || p.idAnimal;
          opt.textContent = p.nome || p.nomeAnimal;
          selectPet.appendChild(opt);
        });
      }
    } catch (err) {
      console.error(err);
      gridPets.innerHTML = '<p style="color: var(--danger);">Erro ao buscar informações do tutor.</p>';
    }
  });

  // 4. ENVIO DO FORMULÁRIO DE AGENDAMENTO
  const formAgendamento = document.getElementById('form-agendamento');
  formAgendamento.addEventListener('submit', async (e) => {
    e.preventDefault();

    const novoAgendamento = {
      idAnimal: document.getElementById('select-pet-agendamento').value,
      idVeterinario: document.getElementById('select-vet-agendamento').value,
      data: document.getElementById('data-agendamento').value,
      horario: document.getElementById('hora-agendamento').value,
      motivo: document.getElementById('motivo-consulta').value
    };

    try {
      await api.criarAtendimento(novoAgendamento);
      alert('Agendamento realizado com sucesso!');
      formAgendamento.reset();
    } catch (err) {
      alert('Erro ao agendar: ' + err.message);
    }
  });
});