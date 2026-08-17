package com.patasfelizes.api.entities;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TipoExame")
public class EntityTipoExame {
    @Id
    @Column(name = "nroTipoExame")
    private Integer nroTipoExame;

    @Column(name = "nomeTipoExame")
    private String nomeTipoExame;

    public Integer getNroTipoExame() {
        return nroTipoExame;
    }

    public void setNroTipoExame(Integer nroTipoExame) {
        this.nroTipoExame = nroTipoExame;
    }

    public String getNomeTipoExame() {
        return nomeTipoExame;
    }

    public void setNomeTipoExame(String nomeTipoExame) {
        this.nomeTipoExame = nomeTipoExame;
    }

    public EntityTipoExame(Integer nroTipoExame, String nomeTipoExame) {
        this.nroTipoExame = nroTipoExame;
        this.nomeTipoExame = nomeTipoExame;
    }
}