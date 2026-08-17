package com.patasfelizes.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Especie")
public class EntityEspecie {
    @Id
    @Column(name = "nroEspecie")
    private Integer nroEspecie;

    @Column(name = "nomeEspecie")
    private String nomeEspecie;

    public Integer getNroEspecie() {
        return nroEspecie;
    }

    public void setNroEspecie(Integer nroEspecie) {
        this.nroEspecie = nroEspecie;
    }

    public String getNomeEspecie() {
        return nomeEspecie;
    }

    public void setNomeEspecie(String nomeEspecie) {
        this.nomeEspecie = nomeEspecie;
    }

    public EntityEspecie(Integer nroEspecie, String nomeEspecie) {
        this.nroEspecie = nroEspecie;
        this.nomeEspecie = nomeEspecie;
    }
}
