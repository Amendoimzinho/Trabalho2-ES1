package com.patasfelizes.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "PrescricaoExame")
public class EntityPrescricaoExame {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nroConsulta", nullable = false)
    private EntityAtendimentoConsulta consulta;

    @ManyToOne
    @JoinColumn(name = "nroTipoExame", nullable = false)
    private EntityTipoExame tipoExame;

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

    public EntityTipoExame getTipoExame() {
        return tipoExame;
    }

    public void setTipoExame(EntityTipoExame tipoExame) {
        this.tipoExame = tipoExame;
    }

    public EntityPrescricaoExame(Integer id, EntityAtendimentoConsulta consulta, EntityTipoExame tipoExame) {
        this.id = id;
        this.consulta = consulta;
        this.tipoExame = tipoExame;
    }
}
