package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityPrescricaoExame;

public interface PrescricaoExameRepository extends JpaRepository<EntityPrescricaoExame, Integer> {
    
}