package com.patasfelizes.api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Veterinario")
public class EntityVeterinario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nroVeterinario")
    private Integer nroVeterinario;

    @Column(name = "nomeVeterinario")
    private String nomeVeterinario;

    @Column(name = "CRMV")
    private String crmv;

    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<EntityHorariosVeterinario> horarios;

    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<EntityEmailVeterinario> emails;

    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<EntityTelefoneVeterinario> telefones;
    
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
    
    public java.util.List<EntityHorariosVeterinario> getHorarios() { 
        return horarios; 
    }
    
    public void setHorarios(java.util.List<EntityHorariosVeterinario> horarios) { 
        this.horarios = horarios; 
    }

    public java.util.List<EntityEmailVeterinario> getEmails() {
        return emails;
    }
    
    public void setEmails(java.util.List<EntityEmailVeterinario> emails) {
        this.emails = emails;
    }
    
    public java.util.List<EntityTelefoneVeterinario> getTelefones() {
        return telefones;
    }
    
    public void setTelefones(java.util.List<EntityTelefoneVeterinario> telefones) {
        this.telefones = telefones;
    }

    public EntityVeterinario() {
    }
    
    
}