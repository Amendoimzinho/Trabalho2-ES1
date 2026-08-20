document.addEventListener('DOMContentLoaded', () => {
  // ========================================================
  // 1. NAVEGAÇÃO ENTRE ABAS
  // ========================================================
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

  // ========================================================
  // 2. CARREGAR FILA DE ATENDIMENTOS
  // ========================================================
  async function carregarFilaAtendimentos() {
    const tbody = document.querySelector('#tabela-fila-vets tbody');
    if (!tbody) return;

    try {
      const atendimentos = await api.listarAtendimentos();
      if (!atendimentos || atendimentos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Nenhum atendimento na fila.</td></tr>';
        return;
      }

      tbody.innerHTML = atendimentos.map((a) => {
        let dataExibicao = 'Hoje';
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

        const idRef = a.nroAnimal || '';

        return `
          <tr>
            <td># ${idRef}</td>
            <td>${dataExibicao}</td>
            <td><strong>${a.nomeAnimal || 'Pet'}</strong></td>
            <td>-</td>
            <td><span class="badge-tag">${a.nroTipoAtendimento || 'Consulta'}</span></td>
            <td>
              <button class="btn-primary-yellow btn-atender" data-id="${idRef}" style="padding: 4px 10px; font-size: 0.8rem;">
                Atender
              </button>
            </td>
          </tr>
        `;
      }).join('');

      document.querySelectorAll('.btn-atender').forEach((b) => {
        b.addEventListener('click', (e) => {
          const id = e.target.getAttribute('data-id');
          
          const campoConsulta = document.getElementById('consulta-id-atendimento');
          const campoVacina = document.getElementById('vacina-id-atendimento');
          
          if (campoConsulta) campoConsulta.value = id;
          if (campoVacina) campoVacina.value = id;
          
          const btnConsultaTab = document.querySelector('[data-target="secao-consulta"]');
          if (btnConsultaTab) btnConsultaTab.click();
        });
      });

    } catch (err) {
      console.error(err);
      tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color: red;">Erro ao carregar fila.</td></tr>';
    }
  }

  // ========================================================
  // 3. REGISTRAR CONSULTA (Prontuário salvo em "observacoes")
  // ========================================================
  const formConsulta = document.getElementById('form-registro-consulta');
  if (formConsulta) {
    formConsulta.addEventListener('submit', async (e) => {
      e.preventDefault();
      
      const btnSubmit = formConsulta.querySelector('button[type="submit"]');
      btnSubmit.innerText = 'Salvando...';
      btnSubmit.disabled = true;

      const peso = document.getElementById('consulta-peso').value;
      const temp = document.getElementById('consulta-temperatura').value;
      const diag = document.getElementById('consulta-diagnostico').value;
      const presc = document.getElementById('consulta-prescricao').value;
      
      const textoProntuario = `[PRONTUÁRIO] Peso: ${peso}kg | Temp: ${temp}°C | Diag: ${diag} | Presc: ${presc}`;

      const novoRegistro = {
        nroAnimal: parseInt(document.getElementById('consulta-id-atendimento').value) || 0,
        observacoes: textoProntuario,
        nroTipoAtendimento: 2 // VOLTOU: ID 2 indica "Consulta Finalizada/Prontuário"
      };

      try {
        await api.criarAtendimento(novoRegistro);
        alert('✅ Prontuário clínico salvo com sucesso no banco!');
        formConsulta.reset();
        carregarFilaAtendimentos();
      } catch (err) {
        alert('❌ Erro ao salvar prontuário: ' + err.message);
      } finally {
        btnSubmit.innerText = 'Finalizar e Salvar Consulta';
        btnSubmit.disabled = false;
      }
    });
  }

  // ========================================================
  // 4. REGISTRAR VACINAÇÃO (Salvo em "observacoes")
  // ========================================================
  const formVacina = document.getElementById('form-registro-vacina');
  if (formVacina) {
    formVacina.addEventListener('submit', async (e) => {
      e.preventDefault();
      
      const btnSubmit = formVacina.querySelector('button[type="submit"]');
      btnSubmit.innerText = 'Salvando...';
      btnSubmit.disabled = true;

      const nomeVacina = document.getElementById('vacina-nome').value;
      const lote = document.getElementById('vacina-lote').value;
      const proxDose = document.getElementById('vacina-proxima-dose').value;
      
      const textoVacina = `[VACINA] Nome: ${nomeVacina} | Lote: ${lote} | Próx. Dose: ${proxDose}`;

      const novoRegistro = {
        nroAnimal: parseInt(document.getElementById('vacina-id-atendimento').value) || 0,
        observacoes: textoVacina,
        nroTipoAtendimento: 3 // VOLTOU: ID 3 indica "Vacinação"
      };

      try {
        await api.criarAtendimento(novoRegistro);
        alert('✅ Registro de vacinação salvo com sucesso!');
        formVacina.reset();
        carregarFilaAtendimentos();
      } catch (err) {
        alert('❌ Erro ao salvar vacina: ' + err.message);
      } finally {
        btnSubmit.innerText = 'Salvar Registro de Vacina';
        btnSubmit.disabled = false;
      }
    });
  }

  // Inicializa a fila
  carregarFilaAtendimentos();
});