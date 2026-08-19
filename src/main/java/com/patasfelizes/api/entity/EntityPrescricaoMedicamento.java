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
@Table(name = "PrescricaoMedicamento")
public class EntityPrescricaoMedicamento {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nroConsulta", nullable = false)
    private EntityAtendimentoConsulta consulta;

    @ManyToOne
    @JoinColumn(name = "nroMedicacao", nullable = false)
    private EntityMedicamento medicamento;

    @Column(name = "dosagem")
    private Double dosagem;

    @ManyToOne
    @JoinColumn(name = "nroUNMedida", nullable = false)
    private EntityUNMedida unidadeMedida;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EntityAtendimentoConsulta getConsulta() {
        return consulta;
    }

    public void setConsulta(EntityAtendimentoConsulta consulta) {
        this.consulta = consulta;
    }

    public EntityMedicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(EntityMedicamento medicamento) {
        this.medicamento = medicamento;
    }

    public Double getDosagem() {
        return dosagem;
    }

    public void setDosagem(Double dosagem) {
        this.dosagem = dosagem;
    }

    public EntityUNMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(EntityUNMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public EntityPrescricaoMedicamento() {
    }


}
