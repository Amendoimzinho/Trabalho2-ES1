package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "AtendimentoConsulta")
public class EntityAtendimentoConsulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public EntityAtendimentoConsulta() {
    }



    
}