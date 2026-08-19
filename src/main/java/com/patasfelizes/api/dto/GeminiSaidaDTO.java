package com.patasfelizes.api.dto;

public class GeminiSaidaDTO {
    private String resposta;
    private String modeloUsado;
    private Long tempoProcessamento;
    private Integer totalTokens;
    private String acaoRealizada;  // Qual endpoint foi chamado

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }
    
    public String getModeloUsado() { return modeloUsado; }
    public void setModeloUsado(String modeloUsado) { this.modeloUsado = modeloUsado; }
    
    public Long getTempoProcessamento() { return tempoProcessamento; }
    public void setTempoProcessamento(Long tempoProcessamento) { 
        this.tempoProcessamento = tempoProcessamento; 
    }
    
    public Integer getTotalTokens() { return totalTokens; }
    public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
    
    public String getAcaoRealizada() { return acaoRealizada; }
    public void setAcaoRealizada(String acaoRealizada) { this.acaoRealizada = acaoRealizada; }
}
