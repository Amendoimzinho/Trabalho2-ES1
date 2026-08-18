package com.patasfelizes.api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityAtendimento;
import com.patasfelizes.api.entity.EntityVeterinario;

public interface AtendimentoRepository extends JpaRepository<EntityAtendimento, Integer> {
    // Usado no ServiceVeterinario para calcular horários livres
    List<EntityAtendimento> findByVeterinario(EntityVeterinario veterinario);
}