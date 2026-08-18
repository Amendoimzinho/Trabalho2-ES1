package com.patasfelizes.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patasfelizes.api.entity.EntityVacina;

public interface VacinaRepository extends JpaRepository<EntityVacina, Integer> {
    Optional<EntityVacina> findByNomeVacinaIgnoreCase(String nomeVacina);
}