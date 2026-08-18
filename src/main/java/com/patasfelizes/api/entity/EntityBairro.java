package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "Bairro")
public class EntityBairro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public EntityBairro() {
    }



    
}