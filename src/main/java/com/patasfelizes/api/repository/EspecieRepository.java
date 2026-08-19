package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityEspecie;

public interface EspecieRepository extends JpaRepository<EntityEspecie, Integer> {
    
}