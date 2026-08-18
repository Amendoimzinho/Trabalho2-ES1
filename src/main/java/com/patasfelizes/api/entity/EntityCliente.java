package com.patasfelizes.api.entity;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityEmailCliente> emails;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityEnderecoCliente> enderecos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityTelefoneCliente> telefones;

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

    public List<EntityEmailCliente> getEmails() {
        return emails;
    }

    public void setEmails(List<EntityEmailCliente> emails) { 
        this.emails = emails; 
    }

    public List<EntityEnderecoCliente> getEnderecos() { 
        return enderecos; 
    }

    public void setEnderecos(List<EntityEnderecoCliente> enderecos) { 
        this.enderecos = enderecos; 
    }

    public List<EntityTelefoneCliente> getTelefones() { 
        return telefones; 
    }

    public void setTelefones(List<EntityTelefoneCliente> telefones) { 
        this.telefones = telefones; 
    }

    public EntityCliente() {
    }



    
}