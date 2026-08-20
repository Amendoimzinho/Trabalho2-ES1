const API_BASE_URL = 'http://localhost:8080/api';

const api = {
    // ==== Clientes
    async listarClientes(nomeCliente = null, nroCliente = null, cpfCliente = null) {
        let url = `${API_BASE_URL}/clientes`;
        const params = [];
        
        if (nomeCliente) params.push(`nomeCliente=${encodeURIComponent(nomeCliente)}`);
        if (nroCliente) params.push(`nroCliente=${encodeURIComponent(nroCliente)}`);
        
        if (params.length > 0) {
            url += `?${params.join('&')}`;
        }

        const res = await fetch(url);
        if (!res.ok) throw new Error('Erro ao listar clientes');
        return await res.json();
    },

    async buscarClientePorId(id) {
        const res = await fetch(`${API_BASE_URL}/clientes?nroCliente=${id}`);
        if (!res.ok) throw new Error('Cliente não encontrado');
        return await res.json();
    },

    async buscarClientePorCpf(cpf) {
        const res = await fetch(`${API_BASE_URL}/clientes?nomeCliente=${cpf}`);
        if (!res.ok) throw new Error('Cliente não encontrado');
        return await res.json();

        // antigo
        // const res = await fetch(`${API_BASE_URL}/clientes/buscar/cpf?cpf=${encodeURIComponent(cpf)}`);
        // if (!res.ok) {
        //     const clientes = await this.listarClientes();
        //     const cli = clientes.find(c => (c.cpf || c.CPF) === cpf);
        //     if (!cli) throw new Error('Cliente não encontrado pelo CPF informado');
        //     return cli;
        // } return await res.json();
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
        } return await res.json();
    },

    // ==== Animais
    // NAO IMPLEMENTADO - ENDPOINTS NAO EXISTEM AINDA
    // async listarPets() {
    //     const res = await fetch(`${API_BASE_URL}/clientes/animais`);
    //     if (!res.ok) {
    //     const clientes = await this.listarClientes();
    //     return clientes.flatMap(c => c.animais || c.pets || []);
    //     }
    //     return await res.json();
    // },

    // async criarPet(pet) {
    //     const res = await fetch(`${API_BASE_URL}/clientes/animais`, {
    //     method: 'POST',
    //     headers: { 'Content-Type': 'application/json' },
    //     body: JSON.stringify(pet)
    //     });
    //     if (!res.ok) throw new Error('Erro ao cadastrar animal');
    //     return await res.json();
    // },

    // ===== Veterinarios
    async listarVeterinarios() {
        const res = await fetch(`${API_BASE_URL}/veterinarios`);
        if (!res.ok) throw new Error('Erro ao listar veterinários');
        return await res.json();
    },

    async listarHorariosVeterinario(idVeterinario) {
        const res = await fetch(`${API_BASE_URL}/veterinarios/${idVeterinario}/horarios-disponiveis`);
        if (!res.ok) throw new Error('Erro ao buscar horários do veterinário');
        return await res.json();
    },

    // ==== Atendimentos
    async listarAtendimentos(nomeCliente = null) {
        let url = `${API_BASE_URL}/atendimentos`;
        if (nomeCliente) {
            url += `?nomeCliente=${encodeURIComponent(nomeCliente)}`;
        }

        const res = await fetch(url);
        if (!res.ok) throw new Error('Erro ao listar atendimentos');
        return await res.json();
    },

    async criarAtendimento(atendimento) {
        const res = await fetch(`${API_BASE_URL}/atendimentos`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(atendimento)
        });
        if (!res.ok) {
            const erroServidor = await res.text();
            throw new Error(`Erro ${res.status}: ${erroServidor}`);
        }
        return await res.json();
    },

    // ===== GEMINI
    async consultarGemini(promptTexto) {
        const prompt = {
            mensagem: promptTexto,
            temperatura: 0.7,
            maxTokens: 800
        };

        const res = await fetch(`${API_BASE_URL}/gemini/perguntar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(prompt)
        });

        if (!res.ok) {
            const errorBody = await res.json().catch(() => ({}));
            throw new Error(errorBody.mensagem || 'Erro na comunicação com o assistente inteligente');
        } return await res.json();
    } 
};