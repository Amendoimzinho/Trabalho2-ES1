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
import com.patasfelizes.api.model.Atendimento;
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
    @Autowired private ServiceVeterinario serviceVeterinario;

    public Atendimento agendarAtendimento(Atendimento vo) {
        LocalDateTime horarioEscolhido = LocalDateTime.parse(vo.ini_dataAtendimento);
        List<LocalDateTime> horariosLivres = serviceVeterinario.calcularHorariosDisponiveis(vo.nroVeterinario);
        
        if (!horariosLivres.contains(horarioEscolhido)) {
            throw new RuntimeException("Erro: Horário indisponível ou fora do expediente do veterinário.");
        }
        
        EntityAnimal animal = animalRepository.findById(vo.nroAnimal)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado."));
        EntityVeterinario veterinario = veterinarioRepository.findById(vo.nroVeterinario)
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado."));

        EntityTipoAtendimento tipo = tipoAtendimentoRepository.findById(vo.nroTipoAtendimento)
                .orElseThrow(() -> new RuntimeException("Tipo de Atendimento base não encontrado."));

        EntityAtendimento atendimento = new EntityAtendimento();

        atendimento.setAnimal(animal);
        atendimento.setVeterinario(veterinario);
        atendimento.setTipoAtendimento(tipo);
        atendimento.setInicioAtendimento(horarioEscolhido);
        atendimento.setFimAtendimento(horarioEscolhido.plusHours(1)); 

        EntityAtendimentoConsulta consulta = new EntityAtendimentoConsulta();

        consulta.setObservacoes(vo.observacoes);
        consulta.setAtendimento(atendimento);
        atendimento.setConsultas(List.of(consulta)); 

        return toVO(atendimentoRepository.save(atendimento));
    }

    public List<Atendimento> listarAtendimentos(String nomeCliente, Integer nroAnimal, Integer nroTipoAtendimento) {
        List<EntityAtendimento> todos = atendimentoRepository.findAll();

        return todos.stream()
            .filter(a -> {
                boolean bateu = true;
                if (nomeCliente != null && !nomeCliente.trim().isEmpty()) {
                    String nomeCadastrado = a.getAnimal().getCliente().getNomeCliente().toLowerCase();
                    if (!nomeCadastrado.contains(nomeCliente.toLowerCase())) bateu = false;
                }
                if (nroAnimal != null && !a.getAnimal().getNroAnimal().equals(nroAnimal)) bateu = false;
                if (nroTipoAtendimento != null && !a.getTipoAtendimento().getNroTipoAtendimento().equals(nroTipoAtendimento)) bateu = false;
                return bateu;
            })
            .map(this::toVO)
            .collect(Collectors.toList());
    }

    private Atendimento toVO(EntityAtendimento entidade) {
        Atendimento vo = new Atendimento();

        vo.nroAtendimento = entidade.getNroAtendimento();
        vo.nroAnimal = entidade.getAnimal().getNroAnimal();
        vo.nomeAnimal = entidade.getAnimal().getNomeAnimal();
        vo.nroVeterinario = entidade.getVeterinario().getNroVeterinario();
        vo.nomeVeterinario = entidade.getVeterinario().getNomeVeterinario();
        vo.nroTipoAtendimento = entidade.getTipoAtendimento().getNroTipoAtendimento();
        
        if (entidade.getInicioAtendimento() != null) vo.ini_dataAtendimento = entidade.getInicioAtendimento().toString();
        if (entidade.getFimAtendimento() != null) vo.end_dataAtendimento = entidade.getFimAtendimento().toString();

        if (entidade.getConsultas() != null && !entidade.getConsultas().isEmpty()) {
            vo.observacoes = entidade.getConsultas().get(0).getObservacoes();
        }
        
        return vo;
    }
}