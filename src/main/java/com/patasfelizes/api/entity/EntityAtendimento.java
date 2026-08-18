package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;

@Entity
@Table(name = "Atendimento")
public class EntityAtendimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nroAtendimento")
    private Integer nroAtendimento;

    @ManyToOne
    @JoinColumn(name = "nroTipoAtendimento", nullable = false)
    private EntityTipoAtendimento tipoAtendimento;

    @ManyToOne
    @JoinColumn(name = "nroAnimal", nullable = false)
    private EntityAnimal animal;

    @ManyToOne
    @JoinColumn(name = "nroVeterinario", nullable = false)
    private EntityVeterinario veterinario;

    @Column(name = "ini_dataAtendimento")
    private LocalDateTime inicioAtendimento;

    @Column(name = "end_dataAtendimento")
    private LocalDateTime fimAtendimento;

    public Integer getNroAtendimento() {
        return nroAtendimento;
    }

    public void setNroAtendimento(Integer nroAtendimento) {
        this.nroAtendimento = nroAtendimento;
    }

    public EntityTipoAtendimento getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(EntityTipoAtendimento tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public EntityAnimal getAnimal() {
        return animal;
    }

    public void setAnimal(EntityAnimal animal) {
        this.animal = animal;
    }

    public EntityVeterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(EntityVeterinario veterinario) {
        this.veterinario = veterinario;
    }

    public LocalDateTime getInicioAtendimento() {
        return inicioAtendimento;
    }

    public void setInicioAtendimento(LocalDateTime inicioAtendimento) {
        this.inicioAtendimento = inicioAtendimento;
    }

    public LocalDateTime getFimAtendimento() {
        return fimAtendimento;
    }

    public void setFimAtendimento(LocalDateTime fimAtendimento) {
        this.fimAtendimento = fimAtendimento;
    }

    public EntityAtendimento() {
    }



    
}