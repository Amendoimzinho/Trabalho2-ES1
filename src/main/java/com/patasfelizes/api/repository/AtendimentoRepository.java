package com.patasfelizes.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.patasfelizes.api.entity.EntityAtendimento;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AtendimentoRepository extends JpaRepository<EntityAtendimento, Integer> {
    // SELECT * FROM Atendimento WHERE nroVeterinario = ? AND inicioAtendimento BETWEEN ? AND ?
    List<EntityAtendimento> findByVeterinarioNroVeterinarioAndInicioAtendimentoBetween(
        Integer nroVeterinario, 
        LocalDateTime inicioDoDia, 
        LocalDateTime fimDoDia
    );
}