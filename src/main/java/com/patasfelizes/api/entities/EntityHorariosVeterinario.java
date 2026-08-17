package com.patasfelizes.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "HorariosVeterinario")
public class EntityHorariosVeterinario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nroVeterinario", nullable = false)
    private EntityVeterinario veterinario;

    @Column(name = "ini_dataIntervalo")
    private LocalDateTime inicioIntervalo;

    @Column(name = "end_dataIntervalo")
    private LocalDateTime fimIntervalo;

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

    public LocalDateTime getInicioIntervalo() {
        return inicioIntervalo;
    }

    public void setInicioIntervalo(LocalDateTime inicioIntervalo) {
        this.inicioIntervalo = inicioIntervalo;
    }

    public LocalDateTime getFimIntervalo() {
        return fimIntervalo;
    }

    public void setFimIntervalo(LocalDateTime fimIntervalo) {
        this.fimIntervalo = fimIntervalo;
    }

    public EntityHorariosVeterinario(Integer id, EntityVeterinario veterinario, LocalDateTime inicioIntervalo,
            LocalDateTime fimIntervalo) {
        this.id = id;
        this.veterinario = veterinario;
        this.inicioIntervalo = inicioIntervalo;
        this.fimIntervalo = fimIntervalo;
    }
}