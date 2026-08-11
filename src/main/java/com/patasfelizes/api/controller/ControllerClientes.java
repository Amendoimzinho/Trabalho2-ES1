package com.patasfelizes.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.patasfelizes.api.model.Cliente;
import com.patasfelizes.api.service.ServiceClientes;

@RestController
@RequestMapping("/api/clientes")
public class ControllerClientes {

    @Autowired
    private ServiceClientes service;

    @GetMapping
    public List<Cliente> listarClientes(
        @RequestParam(required=false) String nomeCliente,
        @RequestParam(required=false) Integer nroCliente
    ) {
        return service.listarClientes(nomeCliente, nroCliente);
    }

    @PostMapping
    public Cliente criarCliente(@RequestBody Cliente cliente) {
        return service.criarCliente(cliente);
    }


}
