package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityAtendimentoConsulta;

public interface AtendimentoConsultaRepository extends JpaRepository<EntityAtendimentoConsulta, Integer> {
    
}