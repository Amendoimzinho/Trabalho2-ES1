package com.patasfelizes.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.patasfelizes.api.model.Cliente;
import com.patasfelizes.api.service.ServiceClientes;

@RestController
@RequestMapping("/clientes")
public class ControllerClientes {

    @Autowired
    private ServiceClientes serviceClientes;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(
            @RequestParam(required = false) String nomeCliente,
            @RequestParam(required = false) Integer nroCliente) {

        List<Cliente> resultado = serviceClientes.listarClientes(nomeCliente, nroCliente);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<Cliente> criarCliente(@RequestBody Cliente clienteVO) {
        Cliente criado = serviceClientes.criarCliente(clienteVO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
}