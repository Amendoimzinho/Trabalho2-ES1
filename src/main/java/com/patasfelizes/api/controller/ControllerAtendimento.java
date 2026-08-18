// package com.patasfelizes.api.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.patasfelizes.api.model.Atendimento;
// import com.patasfelizes.api.service.ServiceAtendimento;

// @RestController
// @RequestMapping("/api/atendimentos")
// public class ControllerAtendimento {
//     @Autowired
//     private ServiceAtendimento service;

//     @GetMapping
//     public List<Atendimento> listarAtendimentos(
//         @RequestParam(required=false) String nomeCliente,
//         @RequestParam(required=false) Integer nroAnimal,
//         @RequestParam(required=false) Integer nroTipoAtendimento){
//         return service.listarAtendimentos(nomeCliente,nroAnimal,nroTipoAtendimento);
//     }

//     @PutMapping
//     public Atendimento criarAtendimento(@RequestBody Atendimento atendimento) {
//         return service.criarAtendimento(atendimento);
//     }

// }
