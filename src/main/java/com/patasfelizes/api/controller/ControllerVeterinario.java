package com.patasfelizes.api.controller;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import com.patasfelizes.api.model.Veterinario;
import com.patasfelizes.api.service.ServiceVeterinario;
 
@RestController
@RequestMapping("/veterinarios")
public class ControllerVeterinario {
 
    @Autowired
    private ServiceVeterinario serviceVeterinario;
 
    @GetMapping
    public ResponseEntity<List<Veterinario>> listarVeterinarios(
            @RequestParam(required = false) String nomeVeterinario,
            @RequestParam(required = false) Integer nroVeterinario) {
 
        List<Veterinario> resultado = serviceVeterinario.listarVeterinarios(
                nomeVeterinario, nroVeterinario);
 
        return ResponseEntity.ok(resultado);
    }
}