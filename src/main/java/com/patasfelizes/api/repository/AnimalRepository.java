package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityAnimal;

public interface AnimalRepository extends JpaRepository<EntityAnimal, Integer> {
    
}