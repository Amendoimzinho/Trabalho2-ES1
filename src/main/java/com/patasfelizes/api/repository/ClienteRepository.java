package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patasfelizes.api.entity.EntityCliente;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<EntityCliente, Integer> {
    List<EntityCliente> findByNomeClienteContainingIgnoreCase(String nome);
}