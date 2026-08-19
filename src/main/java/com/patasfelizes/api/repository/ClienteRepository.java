package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patasfelizes.api.entity.EntityCliente;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<EntityCliente, Integer> {
    // SELECT * FROM Cliente where nomeCliente = ? IGNORECASE
    List<EntityCliente> findByNomeClienteContainingIgnoreCase(String nome);
}