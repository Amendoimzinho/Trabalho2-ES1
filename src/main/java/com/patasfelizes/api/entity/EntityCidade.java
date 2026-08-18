package com.patasfelizes.api.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "Cidade")
public class EntityCidade {
    @Id
    @Column(name = "nroCidade")
    private Integer nroCidade;

    @Column(name = "nomeCidade")
    private String nomeCidade;

    @ManyToOne
    @JoinColumn(name = "nroEstado", nullable = false)
    private EntityEstado estado;

    public Integer getNroCidade() {
        return nroCidade;
    }

    public void setNroCidade(Integer nroCidade) {
        this.nroCidade = nroCidade;
    }

    public String getNomeCidade() {
        return nomeCidade;
    }

    public void setNomeCidade(String nomeCidade) {
        this.nomeCidade = nomeCidade;
    }

    public EntityEstado getEstado() {
        return estado;
    }

    public void setEstado(EntityEstado estado) {
        this.estado = estado;
    }

    public EntityCidade() {
    }



    
}