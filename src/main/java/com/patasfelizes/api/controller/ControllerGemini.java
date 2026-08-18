package com.patasfelizes.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.patasfelizes.api.dto.GeminiEntradaDTO;
import com.patasfelizes.api.dto.GeminiSaidaDTO;
import com.patasfelizes.api.service.ServiceGemini;

@RestController
@RequestMapping("/api/gemini")
public class ControllerGemini {
    
    @Autowired
    private ServiceGemini serviceGemini;

     @GetMapping("/perguntar")
    public ResponseEntity<GeminiSaidaDTO> perguntarGet(
            @RequestParam String mensagem,
            @RequestParam(required = false) Double temperatura,
            @RequestParam(required = false) Integer maxTokens) {
        
        GeminiEntradaDTO entrada = new GeminiEntradaDTO();
        entrada.setMensagem(mensagem);
        if (temperatura != null) entrada.setTemperatura(temperatura);
        if (maxTokens != null) entrada.setMaxTokens(maxTokens);
        
        try {
            GeminiSaidaDTO saida = serviceGemini.chamarGemini(entrada);
            return ResponseEntity.ok(saida);
            
        } catch (Exception e) {
            GeminiSaidaDTO erro = new GeminiSaidaDTO();
            erro.setResposta("Erro: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }
    
    @PostMapping("/perguntar")
    public ResponseEntity<GeminiSaidaDTO> perguntar(@RequestBody GeminiEntradaDTO entrada) {
        if (entrada.getMensagem() == null || entrada.getMensagem().trim().isEmpty()) {
            GeminiSaidaDTO erro = new GeminiSaidaDTO();
            erro.setResposta("Por favor, envie uma mensagem para o assistente.");
            return ResponseEntity.badRequest().body(erro);
        }
        
        try {
            GeminiSaidaDTO saida = serviceGemini.chamarGemini(entrada);
            return ResponseEntity.ok(saida);
            
        } catch (Exception e) {
            GeminiSaidaDTO erro = new GeminiSaidaDTO();
            erro.setResposta("Desculpe, ocorreu um erro: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }
}
