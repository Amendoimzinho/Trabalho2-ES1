package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Estado")
public class EntityEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public EntityEstado() {
    }


}