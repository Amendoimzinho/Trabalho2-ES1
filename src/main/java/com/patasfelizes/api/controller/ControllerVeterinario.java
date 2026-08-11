package com.patasfelizes.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.patasfelizes.api.model.Veterinario;
import com.patasfelizes.api.service.ServiceVeterinario;

@RestController
@RequestMapping("/api/veterinarios")
public class ControllerVeterinario {
    @Autowired
    private ServiceVeterinario service;

    @GetMapping
    public List<Veterinario> listaVeterinarios(
        @RequestParam(required=false) Integer nroVeterinario,
        @RequestParam(required=false) String nomeVeterinario
    ){
        return service.listarVeterinarios(nroVeterinario, nomeVeterinario);
    }

    
}
