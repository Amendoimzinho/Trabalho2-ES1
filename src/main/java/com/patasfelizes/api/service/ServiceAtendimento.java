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

    // ==========================================
    // CASO DE USO: Agendar Novo Atendimento
    // ==========================================
    public AtendimentoConsulta agendarAtendimento(AtendimentoConsulta vo) {
        EntityAtendimento atendimento = new EntityAtendimento();

        // 1. Buscando as instâncias no banco de dados para criar os vínculos (Foreign Keys)
        EntityAnimal animal = animalRepository.findById(vo.nroAnimal)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado."));
        EntityVeterinario veterinario = veterinarioRepository.findById(vo.nroVeterinario)
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado."));
        
        // Vamos supor que o ID 1 seja sempre 'Consulta'
        EntityTipoAtendimento tipo = tipoAtendimentoRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Tipo de Atendimento base não encontrado."));

        // 2. Preenchendo os dados base do Atendimento
        atendimento.setAnimal(animal);
        atendimento.setVeterinario(veterinario);
        atendimento.setTipoAtendimento(tipo);
        
        if (vo.ini_dataAtendimento != null) {
            atendimento.setInicioAtendimento(LocalDateTime.parse(vo.ini_dataAtendimento));
            // Fim do atendimento = 1 hora depois, por padrão
            atendimento.setFimAtendimento(atendimento.getInicioAtendimento().plusHours(1)); 
        }

        // 3. Criando o vínculo com a tabela filha (AtendimentoConsulta)
        EntityAtendimentoConsulta consulta = new EntityAtendimentoConsulta();
        consulta.setObservacoes(vo.observacoes);
        consulta.setAtendimento(atendimento); // Vincula o pai na filha

        // Como adicionamos o CascadeType.ALL, isso vai mandar salvar as duas tabelas
        atendimento.setConsultas(List.of(consulta)); 

        EntityAtendimento salvo = atendimentoRepository.save(atendimento);
        return converterParaVO(salvo);
    }

    // ==========================================
    // CASO DE USO: Pesquisar Atendimentos Agendados
    // ==========================================
    public List<AtendimentoConsulta> pesquisarAtendimentos(String nomeCliente, Integer nroAnimal, Integer nroTipoAtendimento) {
        
        // Traz todos os registros base
        List<EntityAtendimento> todos = atendimentoRepository.findAll();

        // Aplica os filtros exigidos pelo ator (Atendente)
        return todos.stream()
            .filter(a -> {
                boolean bateu = true;
                
                // Filtro 1: Nome do Cliente (Puxa pela relação Atendimento -> Animal -> Cliente)
                if (nomeCliente != null && !nomeCliente.trim().isEmpty()) {
                    String nomeCadastrado = a.getAnimal().getCliente().getNomeCliente().toLowerCase();
                    if (!nomeCadastrado.contains(nomeCliente.toLowerCase())) bateu = false;
                }
                
                // Filtro 2: Número do Animal
                if (nroAnimal != null) {
                    if (!a.getAnimal().getNroAnimal().equals(nroAnimal)) bateu = false;
                }
                
                // Filtro 3: Tipo do Atendimento
                if (nroTipoAtendimento != null) {
                    if (!a.getTipoAtendimento().getNroTipoAtendimento().equals(nroTipoAtendimento)) bateu = false;
                }
                
                return bateu;
            })
            .map(this::converterParaVO)
            .collect(Collectors.toList());
    }

    // ==========================================
    // MAPPERS
    // ==========================================
    private AtendimentoConsulta converterParaVO(EntityAtendimento entidade) {
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