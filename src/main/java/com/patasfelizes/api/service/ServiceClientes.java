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

    public List<Cliente> listarClientes() {
        return dao.listarCliente();
    }

    public List<Cliente> buscarCliente(String nome) {
        return dao.listarCliente(nome);
    }

    public List<Cliente> buscarCliente(Integer id) {
        return dao.listarCliente(id);
    }

    public Cliente criarCliente(Cliente cliente) {
        return dao.criarCliente(cliente);
    }
}
