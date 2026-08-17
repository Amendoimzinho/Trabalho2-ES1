package com.patasfelizes.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patasfelizes.api.model.Atendimento;
import com.patasfelizes.api.repositories.DAO;

@Service
public class ServiceAtendimento {
    @Autowired
    private DAO dao;

    public List<Atendimento> listarAtendimentos(String nomeCliente, Integer nroAnimal, Integer nroTipoAtendimento) {
        return null;
    }

    public Atendimento criarAtendimento(Atendimento atendimento) {
        return null;
    }
}
