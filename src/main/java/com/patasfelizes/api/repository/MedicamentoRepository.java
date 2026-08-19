package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityMedicamento;

public interface MedicamentoRepository extends JpaRepository<EntityMedicamento, Integer> {
    
}