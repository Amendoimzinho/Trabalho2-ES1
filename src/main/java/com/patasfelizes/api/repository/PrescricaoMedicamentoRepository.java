package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityPrescricaoMedicamento;

public interface PrescricaoMedicamentoRepository extends JpaRepository<EntityPrescricaoMedicamento, Integer> {
    
}