package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Medicamento")
public class EntityMedicamento {
    @Id
    @Column(name = "nroMedicamento")
    private Integer nroMedicamento;

    @Column(name = "nomeMedicamento")
    private String nomeMedicamento;

    @Column(name = "fabricante")
    private String fabricante;

    public Integer getNroMedicamento() {
        return nroMedicamento;
    }

    public void setNroMedicamento(Integer nroMedicamento) {
        this.nroMedicamento = nroMedicamento;
    }

    public String getNomeMedicamento() {
        return nomeMedicamento;
    }

    public void setNomeMedicamento(String nomeMedicamento) {
        this.nomeMedicamento = nomeMedicamento;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public EntityMedicamento(Integer nroMedicamento, String nomeMedicamento, String fabricante) {
        this.nroMedicamento = nroMedicamento;
        this.nomeMedicamento = nomeMedicamento;
        this.fabricante = fabricante;
    }
}
