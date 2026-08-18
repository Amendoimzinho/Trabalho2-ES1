package com.patasfelizes.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patasfelizes.api.entity.EntityAtendimento;
import com.patasfelizes.api.entity.EntityHorariosVeterinario;
import com.patasfelizes.api.entity.EntityVeterinario;
import com.patasfelizes.api.repository.AtendimentoRepository;
import com.patasfelizes.api.repository.VeterinarioRepository;

@Service
@Transactional
public class ServiceVeterinario {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    // ==========================================
    // CASO DE USO: Horários disponíveis
    // ==========================================
    public List<LocalDateTime> calcularHorariosDisponiveis(Integer nroVeterinario) {
        // 1. Busca o Veterinário e seus turnos de trabalho
        EntityVeterinario vet = veterinarioRepository.findById(nroVeterinario)
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado!"));

        List<LocalDateTime> todosHorariosPossiveis = new ArrayList<>();

        // 2. Cria blocos de 1 hora baseados nos intervalos cadastrados na agenda dele
        if (vet.getHorarios() != null) {
            for (EntityHorariosVeterinario turno : vet.getHorarios()) {
                LocalDateTime horaAtual = turno.getInicioIntervalo();
                LocalDateTime fimTurno = turno.getFimIntervalo();

                // Vai somando 1 hora até chegar no fim do expediente
                while (horaAtual.isBefore(fimTurno)) {
                    todosHorariosPossiveis.add(horaAtual);
                    horaAtual = horaAtual.plusHours(1);
                }
            }
        }

        // 3. Busca todos os atendimentos já agendados para este veterinário
        // (Requer a criação de um método findByVeterinario no AtendimentoRepository)
        List<EntityAtendimento> atendimentosMarcados = atendimentoRepository.findByVeterinario(vet);
        
        List<LocalDateTime> horariosOcupados = atendimentosMarcados.stream()
                .map(EntityAtendimento::getInicioAtendimento)
                .collect(Collectors.toList());

        // 4. Subtrai os horários ocupados dos horários possíveis
        todosHorariosPossiveis.removeAll(horariosOcupados);

        return todosHorariosPossiveis;
    }
}