package com.patasfelizes.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TipoAtendimento")
public class EntityTipoAtendimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nroTipoAtendimento")
    private Integer nroTipoAtendimento;

    @Column(name = "nomeTipoAtendimento")
    private String nomeTipoAtendimento;

    public Integer getNroTipoAtendimento() {
        return nroTipoAtendimento;
    }

    public void setNroTipoAtendimento(Integer nroTipoAtendimento) {
        this.nroTipoAtendimento = nroTipoAtendimento;
    }

    public String getNomeTipoAtendimento() {
        return nomeTipoAtendimento;
    }

    public void setNomeTipoAtendimento(String nomeTipoAtendimento) {
        this.nomeTipoAtendimento = nomeTipoAtendimento;
    }

    public EntityTipoAtendimento() {
    }


}
