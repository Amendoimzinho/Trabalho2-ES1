package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UNMedida")
public class EntityUNMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nroUNMedida")
    private Integer nroUNMedida;

    @Column(name = "nomeUNMedida")
    private String nomeUNMedida;

    public Integer getNroUNMedida() {
        return nroUNMedida;
    }

    public void setNroUNMedida(Integer nroUNMedida) {
        this.nroUNMedida = nroUNMedida;
    }

    public String getNomeUNMedida() {
        return nomeUNMedida;
    }

    public void setNomeUNMedida(String nomeUNMedida) {
        this.nomeUNMedida = nomeUNMedida;
    }

    public EntityUNMedida() {
    }


}
