const API_BASE_URL = 'https://trabalho2-es1.onrender.com/api';

const api = {
  // ==========================================
  // 1. CLIENTES & TUTORES
  // ==========================================
  async listarClientes() {
    const res = await fetch(`${API_BASE_URL}/clientes`);
    if (!res.ok) throw new Error('Erro ao listar clientes');
    return await res.json();
  },

  async buscarClientePorId(id) {
    const res = await fetch(`${API_BASE_URL}/clientes/${id}`);
    if (!res.ok) throw new Error('Cliente não encontrado');
    return await res.json();
  },

  async buscarClientePorCpf(cpf) {
    const res = await fetch(`${API_BASE_URL}/clientes/buscar/cpf?cpf=${encodeURIComponent(cpf)}`);
    if (!res.ok) {
      // Fallback: se o endpoint específico não existir, lista e filtra localmente
      const clientes = await this.listarClientes();
      const cli = clientes.find(c => (c.cpf || c.CPF) === cpf);
      if (!cli) throw new Error('Cliente não encontrado pelo CPF informado');
      return cli;
    }
    return await res.json();
  },

    async criarCliente(cliente) {
        const res = await fetch(`${API_BASE_URL}/clientes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(cliente)
        });
        if (!res.ok) {
            const erroServidor = await res.text();
            throw new Error(`Status ${res.status}: ${erroServidor || res.statusText}`);
        }
        return await res.json();
    },

  // ==========================================
  // 2. PETS / ANIMAIS
  // ==========================================
  async listarPets() {
    const res = await fetch(`${API_BASE_URL}/clientes/animais`);
    if (!res.ok) {
      // Fallback se animais ficarem agrupados por clientes
      const clientes = await this.listarClientes();
      return clientes.flatMap(c => c.animais || c.pets || []);
    }
    return await res.json();
  },

  async criarPet(pet) {
    const res = await fetch(`${API_BASE_URL}/clientes/animais`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(pet)
    });
    if (!res.ok) throw new Error('Erro ao cadastrar animal');
    return await res.json();
  },

  // ==========================================
  // 3. VETERINÁRIOS
  // ==========================================
  async listarVeterinarios() {
    const res = await fetch(`${API_BASE_URL}/veterinarios`);
    if (!res.ok) throw new Error('Erro ao listar veterinários');
    return await res.json();
  },

  async criarVeterinario(veterinario) {
    const res = await fetch(`${API_BASE_URL}/veterinarios`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(veterinario)
    });
    if (!res.ok) throw new Error('Erro ao salvar veterinário');
    return await res.json();
  },

  async listarHorariosVeterinario(idVeterinario) {
    const res = await fetch(`${API_BASE_URL}/veterinarios/${idVeterinario}/horarios`);
    if (!res.ok) throw new Error('Erro ao buscar horários do veterinário');
    return await res.json();
  },

  // ==========================================
  // 4. ATENDIMENTOS, CONSULTAS E VACINAS
  // ==========================================
  async listarAtendimentos() {
    const res = await fetch(`${API_BASE_URL}/atendimentos`);
    if (!res.ok) throw new Error('Erro ao listar atendimentos');
    return await res.json();
  },

  async criarAtendimento(atendimento) {
    const res = await fetch(`${API_BASE_URL}/atendimentos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(atendimento)
    });
    if (!res.ok) throw new Error('Erro ao registrar atendimento');
    return await res.json();
  },

  async registrarConsulta(dadosConsulta) {
    const res = await fetch(`${API_BASE_URL}/atendimentos/consulta`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dadosConsulta)
    });
    if (!res.ok) throw new Error('Erro ao registrar detalhes da consulta');
    return await res.json();
  },

  async registrarVacinacao(dadosVacina) {
    const res = await fetch(`${API_BASE_URL}/atendimentos/vacinacao`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dadosVacina)
    });
    if (!res.ok) throw new Error('Erro ao registrar vacinação');
    return await res.json();
  },

  // ==========================================
  // 5. ASSISTENTE DE IA (GEMINI)
  // ==========================================
  async consultarGemini(promptTexto) {
    const prompt = {
      mensagem: promptTexto,
      temperatura: 0.7,
      maxTokens: 800 // Aumentado para evitar corte de respostas longas
    };

    const res = await fetch(`${API_BASE_URL}/gemini/perguntar`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(prompt)
    });

    if (!res.ok) {
      const errorBody = await res.json().catch(() => ({}));
      throw new Error(errorBody.mensagem || 'Erro na comunicação com o assistente inteligente');
    }
    
    return await res.json();
  } 
};