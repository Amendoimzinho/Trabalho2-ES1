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
@Table(name = "Animal") 
public class EntityAnimal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nroAnimal")
    private Integer nroAnimal;

    @ManyToOne
    @JoinColumn(name = "nroTipoAnimal", nullable = false)
    private EntityTipoAnimal tipoAnimal;

    @Column(name = "nomeAnimal")
    private String nomeAnimal;

    @ManyToOne
    @JoinColumn(name = "nroCliente", nullable = false)
    private EntityCliente cliente;

    @Column(name = "genero")
    private Boolean genero;

    @Column(name = "peso")
    private Double peso;

    @Column(name = "Observacoes", columnDefinition = "TEXT")
    private String observacoes;

    public Integer getNroAnimal() {
        return nroAnimal;
    }

    public void setNroAnimal(Integer nroAnimal) {
        this.nroAnimal = nroAnimal;
    }

    public EntityTipoAnimal getTipoAnimal() {
        return tipoAnimal;
    }

    public void setTipoAnimal(EntityTipoAnimal tipoAnimal) {
        this.tipoAnimal = tipoAnimal;
    }

    public String getNomeAnimal() {
        return nomeAnimal;
    }

    public void setNomeAnimal(String nomeAnimal) {
        this.nomeAnimal = nomeAnimal;
    }

    public EntityCliente getCliente() {
        return cliente;
    }

    public void setCliente(EntityCliente cliente) {
        this.cliente = cliente;
    }

    public Boolean getGenero() {
        return genero;
    }

    public void setGenero(Boolean genero) {
        this.genero = genero;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public EntityAnimal() {
    }



    
}