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

  // 2. CARREGAR DADOS GERAIS E KPIs
  async function carregarDashboard() {
    try {
      // Busca paralela para velocidade
      const [clientes, vets, atendimentos, pets] = await Promise.all([
        api.listarClientes().catch(() => []),
        api.listarVeterinarios().catch(() => []),
        api.listarAtendimentos().catch(() => []),
        api.listarPets().catch(() => [])
      ]);

      // Atualiza KPIs
      document.getElementById('kpi-total-clientes').textContent = clientes.length || 0;
      document.getElementById('kpi-total-pets').textContent = pets.length || 0;
      document.getElementById('kpi-total-vets').textContent = vets.length || 0;
      document.getElementById('kpi-total-atendimentos').textContent = atendimentos.length || 0;

      // Renderiza tabelas
      renderizarTabelaUltimos(atendimentos);
      renderizarTabelaTodos(atendimentos);
      renderizarTabelaEquipe(vets);

    } catch (err) {
      console.error('Erro ao montar o painel do gestor:', err);
    }
  }

  // 3. RENDERIZAR TABELAS
  function renderizarTabelaUltimos(lista) {
    const tbody = document.querySelector('#tabela-ultimos-atendimentos tbody');
    if (!tbody) return;

    if (!lista || lista.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Nenhum atendimento recente.</td></tr>';
      return;
    }

    // Pega os 5 mais recentes
    const ultimos = lista.slice(-5).reverse();
    tbody.innerHTML = ultimos.map(a => `
      <tr>
        <td>#${a.id || a.idAtendimento || '-'}</td>
        <td>${a.data || 'Hoje'}</td>
        <td><span class="badge-tag">${a.tipo || 'Consulta'}</span></td>
        <td>${a.nomeAnimal || a.animal?.nome || 'Pet'}</td>
        <td>Dr(a). ${a.nomeVeterinario || a.veterinario?.nome || 'Veterinário'}</td>
      </tr>
    `).join('');
  }

  function renderizarTabelaTodos(lista) {
    const tbody = document.querySelector('#tabela-todos-atendimentos tbody');
    if (!tbody) return;

    if (!lista || lista.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Nenhum registro encontrado.</td></tr>';
      return;
    }

    tbody.innerHTML = lista.map(a => `
      <tr>
        <td>#${a.id || a.idAtendimento || '-'}</td>
        <td>${a.data || '-'} ${a.horario ? 'às ' + a.horario : ''}</td>
        <td><span class="badge-tag">${a.tipo || 'Consulta'}</span></td>
        <td>${a.nomeAnimal || a.animal?.nome || '-'}</td>
        <td>Dr(a). ${a.nomeVeterinario || a.veterinario?.nome || '-'}</td>
      </tr>
    `).join('');
  }

  function renderizarTabelaEquipe(vets) {
    const tbody = document.querySelector('#tabela-equipe-vets tbody');
    if (!tbody) return;

    if (!vets || vets.length === 0) {
      tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">Nenhum veterinário cadastrado.</td></tr>';
      return;
    }

    tbody.innerHTML = vets.map(v => `
      <tr>
        <td>#${v.id || v.idVeterinario || '-'}</td>
        <td><strong>Dr(a). ${v.nome || v.nomeVeterinario || '-'}</strong></td>
        <td><span class="badge-tag">${v.crmv || 'Não informado'}</span></td>
        <td><span style="color: var(--success); font-weight: 600;">● Ativo</span></td>
      </tr>
    `).join('');
  }

  // 4. INTEGRAÇÃO COM IA GEMINI
  const btnIa = document.getElementById('btn-gerar-insights');
  const promptInput = document.getElementById('prompt-ia');
  const outputIa = document.getElementById('saida-ia');

  if (btnIa) {
    btnIa.addEventListener('click', async () => {
      const prompt = promptInput.value.trim();
      if (!prompt) {
        alert('Digite uma pergunta ou instrução para a IA analisar.');
        return;
      }

      outputIa.style.display = 'block';
      outputIa.innerHTML = '<em>Consultando Gemini e analisando métricas da clínica... aguarde um instante.</em>';
      btnIa.disabled = true;

      try {
        const resposta = await api.consultarGemini(prompt);
        // Exibe o texto retornado pelo ControllerGemini
        outputIa.textContent = resposta.resposta || resposta.mensagem || JSON.stringify(resposta, null, 2);
      } catch (err) {
        outputIa.innerHTML = `<span style="color: var(--danger);">Não foi possível obter resposta da IA: ${err.message}</span>`;
      } finally {
        btnIa.disabled = false;
      }
    });
  }

  // Inicializa o dashboard
  carregarDashboard();
});