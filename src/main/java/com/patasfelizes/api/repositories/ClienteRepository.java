package com.patasfelizes.api.repositories;

import com.patasfelizes.api.entities.EntityCliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<EntityCliente, Integer> {
    List<EntityCliente> findByNomeClienteContainingIgnoreCase(String nome);
}