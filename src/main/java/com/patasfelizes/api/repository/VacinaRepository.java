package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityVacina;

public interface VacinaRepository extends JpaRepository<EntityVacina, Integer> {
    
}