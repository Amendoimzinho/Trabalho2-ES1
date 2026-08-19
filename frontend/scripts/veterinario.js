document.addEventListener('DOMContentLoaded', () => {
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

      if (targetId === 'secao-fila') carregarFilaAtendimentos();
    });
  });

  // 2. CARREGAR FILA DE ATENDIMENTOS
  async function carregarFilaAtendimentos() {
    const tbody = document.querySelector('#tabela-fila-vets tbody');
    if (!tbody) return;

    try {
      const atendimentos = await api.listarAtendimentos();
      if (!atendimentos || atendimentos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Nenhum atendimento na fila no momento.</td></tr>';
        return;
      }

      tbody.innerHTML = atendimentos.map((a) => {
        const idAtend = a.id || a.idAtendimento || '-';
        return `
          <tr>
            <td>#${idAtend}</td>
            <td>${a.data || 'Hoje'}</td>
            <td><strong>${a.nomeAnimal || a.animal?.nome || 'Pet'}</strong></td>
            <td>${a.nomeCliente || a.cliente?.nome || '-'}</td>
            <td><span class="badge-tag">${a.tipo || 'Consulta'}</span></td>
            <td>
              <button class="btn-primary-yellow btn-atender" data-id="${idAtend}" style="padding: 4px 10px; font-size: 0.8rem;">
                Atender
              </button>
            </td>
          </tr>
        `;
      }).join('');

      // Botão rápido para preencher o formulário
      document.querySelectorAll('.btn-atender').forEach((b) => {
        b.addEventListener('click', (e) => {
          const id = e.target.getAttribute('data-id');
          document.getElementById('consulta-id-atendimento').value = id;
          document.getElementById('vacina-id-atendimento').value = id;
          
          // Muda para a aba de consulta automaticamente
          const btnConsultaTab = document.querySelector('[data-target="secao-consulta"]');
          if (btnConsultaTab) btnConsultaTab.click();
        });
      });

    } catch (err) {
      console.error(err);
      tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color: red;">Erro ao carregar fila de atendimentos.</td></tr>';
    }
  }

  // 3. REGISTRAR CONSULTA (PRONTUÁRIO)
  const formConsulta = document.getElementById('form-registro-consulta');
  if (formConsulta) {
    formConsulta.addEventListener('submit', async (e) => {
      e.preventDefault();

      const dadosConsulta = {
        idAtendimento: document.getElementById('consulta-id-atendimento').value,
        peso: document.getElementById('consulta-peso').value,
        temperatura: document.getElementById('consulta-temperatura').value,
        diagnostico: document.getElementById('consulta-diagnostico').value,
        prescricao: document.getElementById('consulta-prescricao').value
      };

      try {
        await api.registrarConsulta(dadosConsulta);
        alert('Prontuário clínico salvo com sucesso no banco!');
        formConsulta.reset();
        carregarFilaAtendimentos();
      } catch (err) {
        alert('Não foi possível salvar o prontuário: ' + err.message);
      }
    });
  }

  // 4. REGISTRAR VACINAÇÃO
  const formVacina = document.getElementById('form-registro-vacina');
  if (formVacina) {
    formVacina.addEventListener('submit', async (e) => {
      e.preventDefault();

      const dadosVacina = {
        idAtendimento: document.getElementById('vacina-id-atendimento').value,
        vacina: document.getElementById('vacina-nome').value,
        lote: document.getElementById('vacina-lote').value,
        proximaDose: document.getElementById('vacina-proxima-dose').value
      };

      try {
        await api.registrarVacinacao(dadosVacina);
        alert('Registro de vacinação salvo com sucesso!');
        formVacina.reset();
        carregarFilaAtendimentos();
      } catch (err) {
        alert('Não foi possível salvar a vacina: ' + err.message);
      }
    });
  }

  carregarFilaAtendimentos();
});