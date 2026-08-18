package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityUNMedida;

public interface UNMedidaRepository extends JpaRepository<EntityUNMedida, Integer> {
    
}