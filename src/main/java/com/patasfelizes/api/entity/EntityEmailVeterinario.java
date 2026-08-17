package com.patasfelizes.api.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "EmailVeterinario")
public class EntityEmailVeterinario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nroVeterinario", nullable = false)
    private EntityVeterinario veterinario;

    @Column(name = "edrEmail")
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EntityEmailVeterinario(Integer id, EntityVeterinario veterinario, String email) {
        this.id = id;
        this.veterinario = veterinario;
        this.email = email;
    }
}


