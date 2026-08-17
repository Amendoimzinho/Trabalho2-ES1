package com.patasfelizes.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "Vacina")
public class EntityVacina {
    @Id
    @Column(name = "nroVacina")
    private Integer nroVacina;

    @Column(name = "nomeVacina")
    private String nomeVacina;

    @Column(name = "lote")
    private Integer lote;

    @Column(name = "dataFabricacao")
    private LocalDateTime dataFabricacao;

    @Column(name = "fabricante")
    private String fabricante;

    public Integer getNroVacina() {
        return nroVacina;
    }

    public void setNroVacina(Integer nroVacina) {
        this.nroVacina = nroVacina;
    }

    public String getNomeVacina() {
        return nomeVacina;
    }

    public void setNomeVacina(String nomeVacina) {
        this.nomeVacina = nomeVacina;
    }

    public Integer getLote() {
        return lote;
    }

    public void setLote(Integer lote) {
        this.lote = lote;
    }

    public LocalDateTime getDataFabricacao() {
        return dataFabricacao;
    }

    public void setDataFabricacao(LocalDateTime dataFabricacao) {
        this.dataFabricacao = dataFabricacao;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public EntityVacina(Integer nroVacina, String nomeVacina, Integer lote, LocalDateTime dataFabricacao,
            String fabricante) {
        this.nroVacina = nroVacina;
        this.nomeVacina = nomeVacina;
        this.lote = lote;
        this.dataFabricacao = dataFabricacao;
        this.fabricante = fabricante;
    }
}
