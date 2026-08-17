package com.patasfelizes.api.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cliente")
public class EntityCliente {
    @Id
    @Column(name = "nroCliente")
    private Integer nroCliente;

    @Column(name = "nomeCliente")
    private String nomeCliente;

    @Column(name = "CPF", length = 12)
    private String cpf;

    public Integer getNroCliente() {
        return nroCliente;
    }

    public void setNroCliente(Integer nroCliente) {
        this.nroCliente = nroCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public EntityCliente(Integer nroCliente, String nomeCliente, String cpf) {
        this.nroCliente = nroCliente;
        this.nomeCliente = nomeCliente;
        this.cpf = cpf;
    }

    
}