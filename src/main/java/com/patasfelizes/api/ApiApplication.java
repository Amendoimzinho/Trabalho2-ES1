package com.patasfelizes.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//import com.patasfelizes.api.service.serviceClientes;

import com.patasfelizes.api.repository.ClienteRepository;

@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner testConnDB(ClienteRepository clienteRepository) {
        return args -> {
            System.out.println("Connecting to Neon.tech PostgreSQL...");
            
            try {
                // 1. Instanciando o seu VO e preenchendo os dados
                com.patasfelizes.api.model.Cliente novoCliente = new com.patasfelizes.api.model.Cliente();
                novoCliente.nomeCliente = "Linus Torvalds";
                novoCliente.CPF = "12345678900";
                novoCliente.emails = java.util.List.of("linus@pinguim.com");
                novoCliente.telefones = java.util.List.of("45999999999");
                novoCliente.logradouro = "Rua do Kernel, 42";
                novoCliente.bairro = "Centro";
                novoCliente.cidade = "Foz do Iguaçu";
                novoCliente.estado = "Paraná";
                novoCliente.CEP = "85851000";

                // 2. Testando a Criação (O Service vai converter pra Entity e salvar)
                System.out.println("-> Gravando cliente no banco de dados...");
                com.patasfelizes.api.model.Cliente clienteSalvo = serviceClientes.criarCliente(novoCliente);
                System.out.println("✅ Cliente salvo com sucesso! ID gerado no banco: " + clienteSalvo.nroCliente);

                // 3. Testando a Pesquisa (Buscando o cliente que acabamos de criar)
                System.out.println("\n-> Pesquisando cliente pelo nome 'Linus'...");
                java.util.List<com.patasfelizes.api.model.Cliente> lista = serviceClientes.listarClientes("Linus", null);
                
                System.out.println("✅ " + lista.size() + " cliente(s) encontrado(s).");
                for (com.patasfelizes.api.model.Cliente c : lista) {
                    System.out.println("   - Nome: " + c.nomeCliente);
                    System.out.println("   - CPF: " + c.CPF);
                    System.out.println("   - Cidade: " + c.cidade + " - " + c.estado);
                }
                
                System.out.println("========================================================\n");
            } catch (Exception e) {
                System.err.println("CONNECTION FAILED!");
                System.err.println("error: " + e.getMessage() + "\n");
            }
        };
    }
}