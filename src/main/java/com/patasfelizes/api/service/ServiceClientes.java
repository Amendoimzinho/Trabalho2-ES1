package com.patasfelizes.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patasfelizes.api.dao.DAO;
import com.patasfelizes.api.model.Cliente;

@Service
public class ServiceClientes {
    @Autowired
    private DAO dao;

    public List<Cliente> listarClientes(String nomeCliente, Integer nroCliente) {
        return null;
    }
    
    public Cliente criarCliente(Cliente cliente) {
        return null;
    }
}
