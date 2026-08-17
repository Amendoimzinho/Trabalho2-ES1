package com.patasfelizes.api.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "EmailCliente")
public class EntityEmailCliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // PK artificial necessária pro JPA

    @ManyToOne
    @JoinColumn(name = "nroCliente", nullable = false)
    private EntityCliente cliente;

    @Column(name = "edrEmail")
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EntityCliente getCliente() {
        return cliente;
    }

    public void setCliente(EntityCliente cliente) {
        this.cliente = cliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EntityEmailCliente(Integer id, EntityCliente cliente, String email) {
        this.id = id;
        this.cliente = cliente;
        this.email = email;
    }


}
