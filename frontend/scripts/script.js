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

      if (targetId === 'secao-clientes') carregarListaClientes();
      if (targetId === 'secao-veterinarios') carregarListaVeterinarios();
    });
  });

  // 2. CARREGAR TABELAS DO BANCO
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
          <td>${c.id || c.idCliente || '-'}</td>
          <td><strong>${c.nome || c.nomeCliente || '-'}</strong></td>
          <td>${c.cpf || c.CPF || '-'}</td>
          <td>${c.telefone || '-'}</td>
          <td>${c.email || '-'}</td>
        </tr>
      `).join('');
    } catch (err) {
      console.error(err);
      tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: red;">Erro ao carregar dados do servidor.</td></tr>';
    }
  }

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
          <td>${v.id || v.idVeterinario || '-'}</td>
          <td><strong>${v.nome || v.nomeVeterinario || '-'}</strong></td>
          <td><span class="badge-tag">${v.crmv || 'Sem CRMV'}</span></td>
        </tr>
      `).join('');
    } catch (err) {
      console.error(err);
      tbody.innerHTML = '<tr><td colspan="3" style="text-align: center; color: red;">Erro ao carregar dados do servidor.</td></tr>';
    }
  }

  // 3. ENVIO DO FORMULÁRIO DE CLIENTE
  const formCliente = document.getElementById('form-cadastrar-cliente');
  if (formCliente) {
    formCliente.addEventListener('submit', async (e) => {
      e.preventDefault();

      const novoCliente = {
        nome: document.getElementById('cli-nome').value,
        cpf: document.getElementById('cli-cpf').value,
        telefone: document.getElementById('cli-telefone').value,
        email: document.getElementById('cli-email').value,
        cep: document.getElementById('cli-cep').value
      };

      try {
        await api.criarCliente(novoCliente);
        alert('Cliente salvo com sucesso no banco Neon!');
        formCliente.reset();
        carregarListaClientes();
      } catch (err) {
        alert('Não foi possível salvar o cliente: ' + err.message);
      }
    });
  }

  // Carrega os clientes na inicialização
  carregarListaClientes();
});