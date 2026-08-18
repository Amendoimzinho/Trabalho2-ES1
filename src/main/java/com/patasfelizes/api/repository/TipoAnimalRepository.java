package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityTipoAnimal;

public interface TipoAnimalRepository extends JpaRepository<EntityTipoAnimal, Integer> {
    
}