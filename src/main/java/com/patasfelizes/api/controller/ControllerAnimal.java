// package com.patasfelizes.api.controller;

// import java.time.LocalDateTime;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.patasfelizes.api.model.Animal;
// import com.patasfelizes.api.service.ServiceAnimal;


// @RestController
// @RequestMapping("/api/pets")
// public class ControllerAnimal {
//     @Autowired
//     private ServiceAnimal service;

//     @GetMapping
//     public List<Animal> listarAnimais(
//         @RequestParam(required=false) Integer nroAnimal,
//         @RequestParam(required=false) String nomeAnimal,
//         @RequestParam(required=false) Integer nroDono,
//         @RequestParam(required=false) Integer nroTipoAnimal){
//             return service.listarVeterinarios(nroAnimal, nomeAnimal, nroDono, nroTipoAnimal);
//     }

//     @GetMapping("/horarios")
//     public List<LocalDateTime> listarHorarios(
//         @RequestParam(required=true) Integer nroVeterinario,
//         @RequestParam(required=false) String nomeVeterinario) {
//             return service.listarHorariosVeterinario(nroVeterinario, nomeVeterinario);
//     }
// }
