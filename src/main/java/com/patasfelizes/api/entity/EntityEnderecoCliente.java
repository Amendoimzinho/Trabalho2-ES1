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
@Table(name = "EnderecoCliente")
public class EntityEnderecoCliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nroCliente", nullable = false)
    private EntityCliente cliente;

    @Column(name = "Cep")
    private String cep;

    @Column(name = "logradouro")
    private String logradouro;

    @ManyToOne
    @JoinColumn(name = "nroBairro", nullable = false)
    private EntityBairro bairro;

    @ManyToOne
    @JoinColumn(name = "nroCidade", nullable = false)
    private EntityCidade cidade;

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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public EntityBairro getBairro() {
        return bairro;
    }

    public void setBairro(EntityBairro bairro) {
        this.bairro = bairro;
    }

    public EntityCidade getCidade() {
        return cidade;
    }

    public void setCidade(EntityCidade cidade) {
        this.cidade = cidade;
    }

    public EntityEnderecoCliente() {
    }


}