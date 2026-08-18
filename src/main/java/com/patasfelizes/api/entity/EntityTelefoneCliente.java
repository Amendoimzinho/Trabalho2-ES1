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
@Table(name = "TelefoneCliente")
public class EntityTelefoneCliente {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nroCliente", nullable = false)
    private EntityCliente cliente;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nroDDD", nullable = false)
    private EntityDDD ddd;

    @Column(name = "telefone", length = 10)
    private String telefone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EntityCliente getCliente() {
        return cliente;
    }

    public void setCliente(EntityCliente cliente) {
        this.cliente = cliente;
    }

    public EntityDDD getDdd() {
        return ddd;
    }

    public void setDdd(EntityDDD ddd) {
        this.ddd = ddd;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public EntityTelefoneCliente() {
    }



}


