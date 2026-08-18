package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityTipoAtendimento;

public interface TipoAtendimentoRepository extends JpaRepository<EntityTipoAtendimento, Integer> {
    
}