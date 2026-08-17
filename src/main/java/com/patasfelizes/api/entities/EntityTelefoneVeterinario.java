package com.patasfelizes.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "TelefoneVeterinario")
public class EntityTelefoneVeterinario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nroVeterinario", nullable = false)
    private EntityVeterinario veterinario;

    @ManyToOne
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

    public EntityVeterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(EntityVeterinario veterinario) {
        this.veterinario = veterinario;
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

    public EntityTelefoneVeterinario(Integer id, EntityVeterinario veterinario, EntityDDD ddd, String telefone) {
        this.id = id;
        this.veterinario = veterinario;
        this.ddd = ddd;
        this.telefone = telefone;
    }
}