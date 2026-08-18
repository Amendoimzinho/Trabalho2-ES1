package com.patasfelizes.api.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DDD")
public class EntityDDD {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nroDDD")
    private Integer nroDDD;

    @Column(name = "DDD")
    private Integer codigoDDD;

    public Integer getNroDDD() {
        return nroDDD;
    }

    public void setNroDDD(Integer nroDDD) {
        this.nroDDD = nroDDD;
    }

    public Integer getCodigoDDD() {
        return codigoDDD;
    }

    public void setCodigoDDD(Integer codigoDDD) {
        this.codigoDDD = codigoDDD;
    }

    public EntityDDD() {
    }


}

