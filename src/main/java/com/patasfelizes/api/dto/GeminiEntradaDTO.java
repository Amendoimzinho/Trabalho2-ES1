package com.patasfelizes.api.dto;

public class GeminiEntradaDTO {
    private String mensagem;
    private Double temperatura;
    private Integer maxTokens;

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    
    public Double getTemperatura() { 
        return temperatura != null ? temperatura : 0.5; 
    }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }
    
    public Integer getMaxTokens() {
        return maxTokens != null ? maxTokens : 500;
    }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
}
