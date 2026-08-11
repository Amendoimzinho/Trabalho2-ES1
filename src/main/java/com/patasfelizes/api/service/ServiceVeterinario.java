package com.patasfelizes.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patasfelizes.api.dao.DAO;
import com.patasfelizes.api.model.Veterinario;

@Service
public class ServiceVeterinario {
    @Autowired
    private DAO dao;

    public List<Veterinario> listarVeterinarios(Integer nroVeterinario, String nomeVeterinario) {
        return null;
    }
}
