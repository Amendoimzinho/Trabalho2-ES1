package com.patasfelizes.api.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "AtendimentoVacinacao")
public class EntityAtendimentoVacinacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nroAtendimento", nullable = false)
    private EntityAtendimento atendimento;

    @ManyToOne
    @JoinColumn(name = "nroVacina", nullable = false)
    private EntityVacina vacina;

    @Column(name = "dataProxVacinacao")
    private LocalDateTime dataProxVacinacao;

    @Column(name = "Observacao", columnDefinition = "TEXT")
    private String observacao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EntityAtendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(EntityAtendimento atendimento) {
        this.atendimento = atendimento;
    }

    public EntityVacina getVacina() {
        return vacina;
    }

    public void setVacina(EntityVacina vacina) {
        this.vacina = vacina;
    }

    public LocalDateTime getDataProxVacinacao() {
        return dataProxVacinacao;
    }

    public void setDataProxVacinacao(LocalDateTime dataProxVacinacao) {
        this.dataProxVacinacao = dataProxVacinacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public EntityAtendimentoVacinacao(Integer id, EntityAtendimento atendimento, EntityVacina vacina,
            LocalDateTime dataProxVacinacao, String observacao) {
        this.id = id;
        this.atendimento = atendimento;
        this.vacina = vacina;
        this.dataProxVacinacao = dataProxVacinacao;
        this.observacao = observacao;
    }

    
}