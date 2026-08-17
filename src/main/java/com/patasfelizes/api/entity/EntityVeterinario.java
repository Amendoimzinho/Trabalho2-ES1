package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Veterinario")
public class EntityVeterinario {
    @Id
    @Column(name = "nroVeterinario")
    private Integer nroVeterinario;

    @Column(name = "nomeVeterinario")
    private String nomeVeterinario;

    @Column(name = "CRMV")
    private String crmv;

    public Integer getNroVeterinario() {
        return nroVeterinario;
    }

    public void setNroVeterinario(Integer nroVeterinario) {
        this.nroVeterinario = nroVeterinario;
    }

    public String getNomeVeterinario() {
        return nomeVeterinario;
    }

    public void setNomeVeterinario(String nomeVeterinario) {
        this.nomeVeterinario = nomeVeterinario;
    }

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public EntityVeterinario(Integer nroVeterinario, String nomeVeterinario, String crmv) {
        this.nroVeterinario = nroVeterinario;
        this.nomeVeterinario = nomeVeterinario;
        this.crmv = crmv;
    }
}