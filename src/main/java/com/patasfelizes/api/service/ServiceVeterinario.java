package com.patasfelizes.api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patasfelizes.api.model.Veterinario;
import com.patasfelizes.api.repository.DAO;

@Service
public class ServiceVeterinario {
    @Autowired
    private DAO dao;

    public List<Veterinario> listarVeterinarios(Integer nroVeterinario, String nomeVeterinario) {
        return null;
    }

    public List<LocalDateTime> listarHorariosVeterinario(Integer nroVeterinario, String nomeVeterinario) {
        return null;
    }
}
