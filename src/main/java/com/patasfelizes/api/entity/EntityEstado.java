package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Estado")
public class EntityEstado {
    @Id
    @Column(name = "nroEstado")
    private Integer nroEstado;

    @Column(name = "nomeEstado")
    private String nomeEstado;

    public Integer getNroEstado() {
        return nroEstado;
    }

    public void setNroEstado(Integer nroEstado) {
        this.nroEstado = nroEstado;
    }

    public String getNomeEstado() {
        return nomeEstado;
    }

    public void setNomeEstado(String nomeEstado) {
        this.nomeEstado = nomeEstado;
    }

    public EntityEstado(Integer nroEstado, String nomeEstado) {
        this.nroEstado = nroEstado;
        this.nomeEstado = nomeEstado;
    }
}