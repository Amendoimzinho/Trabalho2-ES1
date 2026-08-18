package com.patasfelizes.api.service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patasfelizes.api.model.Cliente; // Seu VO
import com.patasfelizes.api.entity.EntityCliente;
import com.patasfelizes.api.entity.EntityEmailCliente;
import com.patasfelizes.api.entity.EntityEnderecoCliente;
import com.patasfelizes.api.entity.EntityBairro;
import com.patasfelizes.api.entity.EntityCidade;
import com.patasfelizes.api.entity.EntityEstado;
import com.patasfelizes.api.repository.ClienteRepository;

@Service
public class ServiceClientes {
    
    @Autowired
    private ClienteRepository repository;

    private Cliente toVO(EntityCliente entidade) {
        Cliente vo = new Cliente();

        vo.nroCliente = entidade.getNroCliente();
        vo.nomeCliente = entidade.getNomeCliente();
        vo.CPF = entidade.getCpf();

        if (entidade.getEmails() != null) {
            vo.emails = entidade.getEmails().stream()
                .map(EntityEmailCliente::getEmail)
                .collect(Collectors.toList());
        }

        if (entidade.getEnderecos() != null && !entidade.getEnderecos().isEmpty()) {
            EntityEnderecoCliente end = entidade.getEnderecos().get(0);
            
            vo.CEP = end.getCep();
            vo.logradouro = end.getLogradouro();
            
            if(end.getCep() != null) 
                vo.CEP = end.getCep();

            if(end.getBairro() != null) 
                vo.bairro = end.getBairro().getNomeBairro();

            if(end.getCidade() != null) {
                vo.cidade = end.getCidade().getNomeCidade();
                if (end.getCidade().getEstado() != null) 
                    vo.estado = end.getCidade().getEstado().getNomeEstado();
            }
        }

        return vo;
    }

    private EntityCliente toEntity(Cliente vo) {
        EntityCliente entidade = new EntityCliente();

        entidade.setNomeCliente(vo.nomeCliente);
        entidade.setCpf(vo.CPF);

        if (vo.emails != null && !vo.emails.isEmpty()) {
            List<EntityEmailCliente> listaEmails = new ArrayList<>();

            for (String emailStr : vo.emails) {
                EntityEmailCliente emailEntity = new EntityEmailCliente();

                emailEntity.setEmail(emailStr);
                emailEntity.setCliente(entidade);

                listaEmails.add(emailEntity);
            }

            entidade.setEmails(listaEmails);
        }

        if (vo.logradouro != null) {
            EntityEnderecoCliente endereco = new EntityEnderecoCliente();

            endereco.setCep(vo.CEP != null ? String.valueOf(vo.CEP) : null);
            endereco.setLogradouro(vo.logradouro);
            endereco.setCliente(entidade);

            if (vo.bairro != null) {
                EntityBairro bairro = new EntityBairro();

                bairro.setNomeBairro(vo.bairro);
                endereco.setBairro(bairro);
            }

            if (vo.cidade != null) {
                EntityCidade cidade = new EntityCidade();

                cidade.setNomeCidade(vo.cidade);
                
                if (vo.estado != null) {
                    EntityEstado estado = new EntityEstado();

                    estado.setNomeEstado(vo.estado);
                    cidade.setEstado(estado);
                }
                endereco.setCidade(cidade);
            }

            entidade.setEnderecos(List.of(endereco));
        }

        return entidade;
    }
    
    public Cliente criarCliente(Cliente clienteVO) { 
        return toVO(repository.save(toEntity(clienteVO)));
    }

    public List<Cliente> listarClientes(String nomeCliente, Integer nroCliente) {
        List<EntityCliente> listaEntidades;

        if (nroCliente != null)
            listaEntidades = repository.findById(nroCliente).stream().toList();

        else if (nomeCliente != null && !nomeCliente.trim().isEmpty()) 
            listaEntidades = repository.findByNomeClienteContainingIgnoreCase(nomeCliente);
        
        else
            listaEntidades = repository.findAll();

        return listaEntidades.stream().map(this::toVO).collect(Collectors.toList());
    }
}