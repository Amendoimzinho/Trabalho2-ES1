package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.patasfelizes.api.entity.EntityVeterinario;

public interface VeterinarioRepository extends JpaRepository<EntityVeterinario, Integer> {
    List<EntityVeterinario> findByNomeVeterinarioContainingIgnoreCase(String nomeVeterinario);
}