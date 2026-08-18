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
import com.patasfelizes.api.model.Veterinario;
import com.patasfelizes.api.repository.AtendimentoRepository;
import com.patasfelizes.api.repository.VeterinarioRepository;

@Service
@Transactional
public class ServiceVeterinario {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    
    public List<Veterinario> listarVeterinarios(String nomeVeterinario, Integer nroVeterinario) {
        List<EntityVeterinario> listaEntidades;
 
        if (nroVeterinario != null)
            listaEntidades = veterinarioRepository.findById(nroVeterinario).stream().toList();
 
        else if (nomeVeterinario != null && !nomeVeterinario.trim().isEmpty())
            listaEntidades = veterinarioRepository.findByNomeVeterinarioContainingIgnoreCase(nomeVeterinario);
 
        else
            listaEntidades = veterinarioRepository.findAll();
 
        return listaEntidades.stream().map(this::toVO).collect(Collectors.toList());
    }
 
    private Veterinario toVO(EntityVeterinario entidade) {
        Veterinario vo = new Veterinario();
        
        vo.nroVeterinario = entidade.getNroVeterinario();
        vo.nome = entidade.getNomeVeterinario();
        vo.CRMV = entidade.getCrmv();
        
        return vo;
    }

    public List<LocalDateTime> calcularHorariosDisponiveis(Integer nroVeterinario) {
        EntityVeterinario vet = veterinarioRepository.findById(nroVeterinario)
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado!"));

        List<LocalDateTime> todosHorariosPossiveis = new ArrayList<>();

        if (vet.getHorarios() != null) {
            for (EntityHorariosVeterinario turno : vet.getHorarios()) {
                LocalDateTime horaAtual = turno.getInicioIntervalo();
                LocalDateTime fimTurno = turno.getFimIntervalo();

                while (horaAtual.isBefore(fimTurno)) {
                    todosHorariosPossiveis.add(horaAtual);
                    horaAtual = horaAtual.plusHours(1);
                }
            }
        }

        List<EntityAtendimento> atendimentosMarcados = atendimentoRepository.findByVeterinario(vet);
        
        List<LocalDateTime> horariosOcupados = atendimentosMarcados.stream()
                .map(EntityAtendimento::getInicioAtendimento)
                .collect(Collectors.toList());

        todosHorariosPossiveis.removeAll(horariosOcupados);

        return todosHorariosPossiveis;
    }
}