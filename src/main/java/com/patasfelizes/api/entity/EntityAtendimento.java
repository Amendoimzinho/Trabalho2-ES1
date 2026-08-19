package com.patasfelizes.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<EntityAtendimentoConsulta> consultas;

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

    public java.util.List<EntityAtendimentoConsulta> getConsultas() { 
        return consultas; 
    }

    public void setConsultas(java.util.List<EntityAtendimentoConsulta> consultas) { 
        this.consultas = consultas; 
    }

    public EntityAtendimento() {
    }



    
}