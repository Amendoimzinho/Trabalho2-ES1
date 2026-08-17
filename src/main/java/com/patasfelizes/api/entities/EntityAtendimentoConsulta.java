package com.patasfelizes.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "AtendimentoConsulta")
public class EntityAtendimentoConsulta {
    @Id
    @Column(name = "nroConsulta")
    private Integer nroConsulta;

    @ManyToOne
    @JoinColumn(name = "nroAtendimento", nullable = false)
    private EntityAtendimento atendimento;

    @Column(name = "Observacoes", columnDefinition = "TEXT")
    private String observacoes;

    public Integer getNroConsulta() {
        return nroConsulta;
    }

    public void setNroConsulta(Integer nroConsulta) {
        this.nroConsulta = nroConsulta;
    }

    public EntityAtendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(EntityAtendimento atendimento) {
        this.atendimento = atendimento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public EntityAtendimentoConsulta(Integer nroConsulta, EntityAtendimento atendimento, String observacoes) {
        this.nroConsulta = nroConsulta;
        this.atendimento = atendimento;
        this.observacoes = observacoes;
    }

    
}