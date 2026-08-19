package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.patasfelizes.api.entity.EntityHorariosVeterinario;

public interface HorariosVeterinarioRepository extends JpaRepository<EntityHorariosVeterinario, Integer> {
    
}