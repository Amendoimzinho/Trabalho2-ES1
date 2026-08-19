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

      if (targetId === 'secao-clientes') carregarListaClientes();
      if (targetId === 'secao-veterinarios') carregarListaVeterinarios();
    });
  });

  // ========================================================
  // 2. MÁSCARAS DE ENTRADA (CPF, TELEFONE, CEP)
  // ========================================================
  const inputCpf = document.getElementById('cli-cpf');
  const inputTelefone = document.getElementById('cli-telefone');
  const inputCep = document.getElementById('cli-cep');

  // Máscara CPF: 000.000.000-00
  inputCpf?.addEventListener('input', (e) => {
    let v = e.target.value.replace(/\D/g, '');
    v = v.replace(/(\d{3})(\d)/, '$1.$2');
    v = v.replace(/(\d{3})(\d)/, '$1.$2');
    v = v.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    e.target.value = v;
  });

  // Máscara Telefone: (00) 00000-0000
  inputTelefone?.addEventListener('input', (e) => {
    let v = e.target.value.replace(/\D/g, '');
    v = v.replace(/^(\d{2})(\d)/g, '($1) $2');
    v = v.replace(/(\d)(\d{4})$/, '$1-$2');
    e.target.value = v;
  });

  // Máscara CEP: 00000-000
  inputCep?.addEventListener('input', (e) => {
    let v = e.target.value.replace(/\D/g, '');
    v = v.replace(/^(\d{5})(\d)/, '$1-$2');
    e.target.value = v;
  });

  // ========================================================
  // 3. CONSULTA AUTOMÁTICA DE CEP E CÓDIGO IBGE (ViaCEP)
  // ========================================================
  inputCep?.addEventListener('blur', async () => {
    const cepLimpo = inputCep.value.replace(/\D/g, '');

    if (cepLimpo.length !== 8) return;

    // Feedback visual
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
        alert('CEP não encontrado. Verifique os dígitos informados.');
        limparCamposEndereco();
        return;
      }

      // Preenche os campos do formulário com dados do IBGE/ViaCEP
      if (campoRua) campoRua.value = dados.logradouro || '';
      if (campoBairro) campoBairro.value = dados.bairro || '';
      if (campoCidade) campoCidade.value = dados.localidade || '';
      if (campoUf) campoUf.value = dados.uf || '';
      if (campoIbge) campoIbge.value = dados.ibge || '';

      // Foca automaticamente no campo do número
      document.getElementById('cli-numero')?.focus();

    } catch (err) {
      console.error('Erro na consulta do CEP:', err);
      alert('Não foi possível consultar o CEP no momento.');
      limparCamposEndereco();
    }
  });

  function limparCamposEndereco() {
    const ids = ['cli-logradouro', 'cli-bairro', 'cli-cidade', 'cli-uf', 'cli-ibge'];
    ids.forEach((id) => {
      const el = document.getElementById(id);
      if (el) el.value = '';
    });
  }

  // ========================================================
  // 4. SUBMISSÃO DO FORMULÁRIO DE CLIENTE
  // ========================================================
  const formCliente = document.getElementById('form-cadastrar-cliente');
  if (formCliente) {
    formCliente.addEventListener('submit', async (e) => {
      e.preventDefault();

      const cpfLimpo = document.getElementById('cli-cpf').value.replace(/\D/g, '');
      const cepLimpo = document.getElementById('cli-cep').value.replace(/\D/g, '');
      const emailDigitado = document.getElementById('cli-email').value.trim();

      // IMPORTANTE: o back-end (classe Cliente.java) espera exatamente estes
      // nomes de campo, com essa mesma capitalização — o Jackson (conversor
      // de JSON do Spring) diferencia maiúsculas de minúsculas. "nome" não
      // vira "nomeCliente" sozinho, "cpf" não vira "CPF" sozinho, e "email"
      // (texto único) não vira "emails" (lista) sozinho — por isso esses
      // valores chegavam como null no banco.
      //
      // Campos que NÃO são enviados porque o back-end não tem onde guardar
      // ainda: telefone, ddd, numero, complemento, ibge. Ver observação no
      // final da conversa sobre isso.
      const novoCliente = {
        nomeCliente: document.getElementById('cli-nome').value.trim(),
        CPF: cpfLimpo,
        emails: emailDigitado ? [emailDigitado] : [],
        CEP: cepLimpo,
        logradouro: document.getElementById('cli-logradouro')?.value || '',
        bairro: document.getElementById('cli-bairro')?.value || '',
        cidade: document.getElementById('cli-cidade')?.value || '',
        estado: document.getElementById('cli-uf')?.value || ''
      };

      try {
        await api.criarCliente(novoCliente);
        alert('✅ Cliente e endereço cadastrados com sucesso no Banco de Dados!');
        formCliente.reset();
        limparCamposEndereco();
        carregarListaClientes();
      } catch (err) {
        console.error('Detalhes do erro:', err);
        alert('❌ Erro retornado pelo servidor:\n' + err.message);
      }
    });
  }

  // ========================================================
  // 5. CARREGAR TABELAS DO BANCO
  // ========================================================
  async function carregarListaClientes() {
    const tbody = document.querySelector('#tabela-clientes tbody');
    if (!tbody) return;

    try {
      const clientes = await api.listarClientes();
      if (!clientes || clientes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: var(--muted-text);">Nenhum cliente cadastrado.</td></tr>';
        return;
      }

      tbody.innerHTML = clientes.map((c) => {
        // Campos reais devolvidos pelo Cliente.java: nroCliente, nomeCliente,
        // CPF, emails (lista), logradouro, bairro, cidade, estado, CEP.
        // Não existe campo de telefone no back-end ainda.
        const cpfRaw = c.CPF || '';
        const cpfFormatado = cpfRaw.length === 11
          ? cpfRaw.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
          : (cpfRaw || '-');

        const cidadeUf = c.cidade ? `${c.cidade} / ${c.estado || ''}` : '-';
        const emailExibido = (c.emails && c.emails.length > 0) ? c.emails[0] : '-';

        return `
          <tr>
            <td>#${c.nroCliente ?? '-'}</td>
            <td><strong>${c.nomeCliente || '-'}</strong></td>
            <td>${cpfFormatado}</td>
            <td>${emailExibido}</td>
            <td>${cidadeUf}</td>
          </tr>
        `;
      }).join('');
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
          <td>#${v.id || v.idVeterinario || '-'}</td>
          <td><strong>Dr(a). ${v.nome || v.nomeVeterinario || '-'}</strong></td>
          <td><span class="badge-tag">${v.crmv || 'Sem CRMV'}</span></td>
        </tr>
      `).join('');
    } catch (err) {
      console.error(err);
      tbody.innerHTML = '<tr><td colspan="3" style="text-align: center; color: red;">Erro ao carregar dados do servidor.</td></tr>';
    }
  }

  // Inicializa listando os clientes ao abrir a tela
  carregarListaClientes();
});