const API_BASE_URL = 'http://localhost:8080';

const api = {
  // Clientes
  async listarClientes() {
    const res = await fetch(`${API_BASE_URL}/clientes`);
    if (!res.ok) throw new Error('Erro ao listar clientes');
    return await res.json();
  },

  async criarCliente(cliente) {
    const res = await fetch(`${API_BASE_URL}/clientes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cliente)
    });
    if (!res.ok) throw new Error('Erro ao salvar cliente');
    return await res.json();
  },

  // Veterinários
  async listarVeterinarios() {
    const res = await fetch(`${API_BASE_URL}/veterinarios`);
    if (!res.ok) throw new Error('Erro ao listar veterinários');
    return await res.json();
  },

  // Atendimentos
  async listarAtendimentos() {
    const res = await fetch(`${API_BASE_URL}/atendimentos`);
    if (!res.ok) throw new Error('Erro ao listar atendimentos');
    return await res.json();
  }
};