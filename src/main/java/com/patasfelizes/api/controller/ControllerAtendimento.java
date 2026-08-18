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

import com.patasfelizes.api.model.AtendimentoConsulta;
import com.patasfelizes.api.service.ServiceAtendimento;

@RestController
@RequestMapping("/atendimentos")
public class ControllerAtendimento {

    @Autowired
    private ServiceAtendimento serviceAtendimento;

    @GetMapping
    public ResponseEntity<List<AtendimentoConsulta>> listarAtendimentos(
            @RequestParam(required = false) String nomeCliente,
            @RequestParam(required = false) Integer nroAnimal,
            @RequestParam(required = false) Integer nroTipoAtendimento) {

        List<AtendimentoConsulta> resultado = serviceAtendimento.pesquisarAtendimentos(
                nomeCliente, nroAnimal, nroTipoAtendimento);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<AtendimentoConsulta> agendarAtendimento(
            @RequestBody AtendimentoConsulta vo) {

        AtendimentoConsulta criado = serviceAtendimento.agendarAtendimento(vo);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
}