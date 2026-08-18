package com.patasfelizes.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.patasfelizes.api.service.ServiceClientes;
import com.patasfelizes.api.model.Cliente;

@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner testConnDB(ServiceClientes serviceClientes) {
        return args -> {
            System.out.println("Connecting to Neon.tech PostgreSQL...");
            
            try {
                Cliente novoCliente = new com.patasfelizes.api.model.Cliente();

                novoCliente.nomeCliente = "Teste da Silva";
                novoCliente.CPF = "12345678900";
                novoCliente.emails = java.util.List.of("ediondo@palerma.com");
                novoCliente.telefones = java.util.List.of("45999999999");
                novoCliente.logradouro = "Rua do Neandertal, 42";
                novoCliente.bairro = "Centro";
                novoCliente.cidade = "Terraria";
                novoCliente.estado = "Pedras Pretas";
                novoCliente.CEP = "85858-585";

                System.out.println("salvando cliente...");
                Cliente clienteSalvo = serviceClientes.criarCliente(novoCliente);
                System.out.println("cliente salvo com o id: " + clienteSalvo.nroCliente);

                System.out.println("\n-> Pesquisando cliente pelo nome 'Teste'");
                java.util.List<Cliente> lista = serviceClientes.listarClientes("Teste", null);
                
                System.out.println("" + lista.size() + " cliente(s) encontrado(s).");
                for (com.patasfelizes.api.model.Cliente c : lista) {
                    System.out.println("   - Nome: " + c.nomeCliente);
                    System.out.println("   - CPF: " + c.CPF);
                    System.out.println("   - Cidade: " + c.cidade + " - " + c.estado);
                }
            } catch (Exception e) {
                System.err.println("CONNECTION FAILED!");
                System.err.println("error: " + e.getMessage() + "\n");
            }
        };
    }
}