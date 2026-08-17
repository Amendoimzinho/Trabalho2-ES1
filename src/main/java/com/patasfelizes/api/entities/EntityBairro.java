package com.patasfelizes.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "Bairro")
public class EntityBairro {
    @Id
    @Column(name = "nroBairro")
    private Integer nroBairro;

    @Column(name = "nomeBairro")
    private String nomeBairro;

    public Integer getNroBairro() {
        return nroBairro;
    }

    public void setNroBairro(Integer nroBairro) {
        this.nroBairro = nroBairro;
    }

    public String getNomeBairro() {
        return nomeBairro;
    }

    public void setNomeBairro(String nomeBairro) {
        this.nomeBairro = nomeBairro;
    }

    public EntityBairro(Integer nroBairro, String nomeBairro) {
        this.nroBairro = nroBairro;
        this.nomeBairro = nomeBairro;
    }

    
}