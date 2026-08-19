package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityAtendimentoVacinacao;

public interface AtendimentoVacinacaoRepository extends JpaRepository<EntityAtendimentoVacinacao, Integer> {
    
}