package com.patasfelizes.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patasfelizes.api.entity.EntityAnimal;
import com.patasfelizes.api.entity.EntityAtendimento;
import com.patasfelizes.api.entity.EntityAtendimentoConsulta;
import com.patasfelizes.api.entity.EntityTipoAtendimento;
import com.patasfelizes.api.entity.EntityVeterinario;
import com.patasfelizes.api.model.AtendimentoConsulta;
import com.patasfelizes.api.repository.AnimalRepository;
import com.patasfelizes.api.repository.AtendimentoRepository;
import com.patasfelizes.api.repository.TipoAtendimentoRepository;
import com.patasfelizes.api.repository.VeterinarioRepository;

@Service
@Transactional
public class ServiceAtendimento {

    @Autowired private AtendimentoRepository atendimentoRepository;
    @Autowired private AnimalRepository animalRepository;
    @Autowired private VeterinarioRepository veterinarioRepository;
    @Autowired private TipoAtendimentoRepository tipoAtendimentoRepository;

    public AtendimentoConsulta agendarAtendimento(AtendimentoConsulta vo) {
        EntityAtendimento atendimento = new EntityAtendimento();

        EntityAnimal animal = animalRepository.findById(vo.nroAnimal)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado."));
        EntityVeterinario veterinario = veterinarioRepository.findById(vo.nroVeterinario)
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado."));
        EntityTipoAtendimento tipo = tipoAtendimentoRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Tipo de Atendimento base não encontrado."));

        atendimento.setAnimal(animal);
        atendimento.setVeterinario(veterinario);
        atendimento.setTipoAtendimento(tipo);
        
        if (vo.ini_dataAtendimento != null) {
            atendimento.setInicioAtendimento(LocalDateTime.parse(vo.ini_dataAtendimento));
            atendimento.setFimAtendimento(atendimento.getInicioAtendimento().plusHours(1)); 
        }

        EntityAtendimentoConsulta consulta = new EntityAtendimentoConsulta();
        consulta.setObservacoes(vo.observacoes);
        consulta.setAtendimento(atendimento);

        atendimento.setConsultas(List.of(consulta)); 

        return toVO(atendimentoRepository.save(atendimento));
    }

    public List<AtendimentoConsulta> pesquisarAtendimentos(String nomeCliente, Integer nroAnimal, Integer nroTipoAtendimento) {
        List<EntityAtendimento> todos = atendimentoRepository.findAll();

        return todos.stream()
            .filter(a -> {
                boolean bateu = true;
            
                if (nomeCliente != null && !nomeCliente.trim().isEmpty()) {
                    String nomeCadastrado = a.getAnimal().getCliente().getNomeCliente().toLowerCase();
                    if (!nomeCadastrado.contains(nomeCliente.toLowerCase())) bateu = false;
                }
                
                if (nroAnimal != null) 
                    if (!a.getAnimal().getNroAnimal().equals(nroAnimal)) bateu = false;
                
                if (nroTipoAtendimento != null) 
                    if (!a.getTipoAtendimento().getNroTipoAtendimento().equals(nroTipoAtendimento)) bateu = false;
                
                return bateu;
            })
            .map(this::toVO)
            .collect(Collectors.toList());
    }

    private AtendimentoConsulta toVO(EntityAtendimento entidade) {
        AtendimentoConsulta vo = new AtendimentoConsulta();
        vo.nroAnimal = entidade.getAnimal().getNroAnimal();
        vo.nomeAnimal = entidade.getAnimal().getNomeAnimal();
        vo.nroVeterinario = entidade.getVeterinario().getNroVeterinario();
        vo.nomeVeterinario = entidade.getVeterinario().getNomeVeterinario();
        vo.tipoAtendimento = entidade.getTipoAtendimento().getNomeTipoAtendimento();
        
        if (entidade.getInicioAtendimento() != null) vo.ini_dataAtendimento = entidade.getInicioAtendimento().toString();
        if (entidade.getFimAtendimento() != null) vo.end_dataAtendimento = entidade.getFimAtendimento().toString();

        if (entidade.getConsultas() != null && !entidade.getConsultas().isEmpty()) {
            vo.observacoes = entidade.getConsultas().get(0).getObservacoes();
        }
        
        return vo;
    }
}