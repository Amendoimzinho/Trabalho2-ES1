package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "TipoAnimal")
public class EntityTipoAnimal {
    @Id
    @Column(name = "nroTipoAnimal")
    private Integer nroTipoAnimal;

    @ManyToOne
    @JoinColumn(name = "nroEspecie", nullable = false)
    private EntityEspecie especie;

    @Column(name = "nomeTipoAnimal")
    private String nomeTipoAnimal;

    public Integer getNroTipoAnimal() {
        return nroTipoAnimal;
    }

    public void setNroTipoAnimal(Integer nroTipoAnimal) {
        this.nroTipoAnimal = nroTipoAnimal;
    }

    public EntityEspecie getEspecie() {
        return especie;
    }

    public void setEspecie(EntityEspecie especie) {
        this.especie = especie;
    }

    public String getNomeTipoAnimal() {
        return nomeTipoAnimal;
    }

    public void setNomeTipoAnimal(String nomeTipoAnimal) {
        this.nomeTipoAnimal = nomeTipoAnimal;
    }

    public EntityTipoAnimal(Integer nroTipoAnimal, EntityEspecie especie, String nomeTipoAnimal) {
        this.nroTipoAnimal = nroTipoAnimal;
        this.especie = especie;
        this.nomeTipoAnimal = nomeTipoAnimal;
    }
}
