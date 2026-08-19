package com.patasfelizes.api.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cidade")
public class EntityCidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nroCidade")
    private Integer nroCidade;

    @Column(name = "nomeCidade")
    private String nomeCidade;

    @ManyToOne(cascade = CascadeType.ALL)
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