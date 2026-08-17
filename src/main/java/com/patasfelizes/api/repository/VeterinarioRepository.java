package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.patasfelizes.api.entity.EntityVeterinario;

@Repository
public interface VeterinarioRepository extends JpaRepository<EntityVeterinario, Integer> {
    // Não precisamos de nenhum método extra aqui por enquanto.
    // Só de herdar o JpaRepository, ele já ganha o save(), findById(), findAll(), deleteById()
}