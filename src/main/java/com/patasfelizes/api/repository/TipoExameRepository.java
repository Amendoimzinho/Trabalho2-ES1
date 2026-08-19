package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityTipoExame;

public interface TipoExameRepository extends JpaRepository<EntityTipoExame, Integer> {
    
}