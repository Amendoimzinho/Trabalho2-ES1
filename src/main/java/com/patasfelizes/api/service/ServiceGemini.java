package com.patasfelizes.api.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.FunctionResponse;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;
import com.google.genai.types.Tool;
import com.patasfelizes.api.dto.GeminiEntradaDTO;
import com.patasfelizes.api.dto.GeminiSaidaDTO;
import com.patasfelizes.api.model.Atendimento;

@Service
public class ServiceGemini {

    private final Client client;
    private final ObjectMapper objectMapper;
    
    // Injeção direta dos seus Services existentes no projeto
    private final ServiceClientes clienteService;
    private final ServiceAtendimento atendimentoService;
    private final ServiceVeterinario veterinarioService;

    // Modelo estável oficial suportado pela SDK Java
    private static final String MODELO = "gemini-flash-latest";

    private static final String SYSTEM_PROMPT = """
        Você é um assistente virtual especializado de uma clínica veterinária chamada "PatasFelizes".
        
        SUA FUNÇÃO:
        - Você ajuda Clientes com dúvidas sobre atendimentos e informações da clínica
        - Você é educado, empático e profissional
        - Você NUNCA dá diagnósticos médicos
        - Você sempre recomenda procurar um médico veterinário para qualquer sintoma
        
        REGRAS IMPORTANTES:
        1. Para informações sobre Clientes, use a função 'buscarCliente'
        2. Para agendar um atendimento/consulta, use a função 'agendarAtendimento'
        3. Para verificar horários disponíveis de um veterinário, use a função 'verificarHorarios'
        4. Para buscar dados de atendimentos existentes, use a função 'buscarAtendimento'
        5. SEMPRE confirme os dados antes de agendar algo
        
        Lembre-se: Você é um assistente amigável e prestativo da Clínica PatasFelizes!
        """;

    public ServiceGemini(
            @Value("${gemini.api.key}") String apiKey,
            ServiceClientes clienteService,
            ServiceAtendimento atendimentoService,
            ServiceVeterinario veterinarioService) {

        System.out.println("Chave de API carregada: " + (apiKey != null && !apiKey.isBlank() ? apiKey.substring(0, 5) + "..." : "NULA"));
        
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

        this.objectMapper = new ObjectMapper();
        this.clienteService = clienteService;
        this.atendimentoService = atendimentoService;
        this.veterinarioService = veterinarioService;
    }

    public String teste() {
        try {
            GenerateContentResponse response = client.models.generateContent(MODELO, "Ping?", null);
            return response.text();
        } catch (Exception e) {
            return "Erro no teste: " + e.getMessage();
        }
    }

    public GeminiSaidaDTO chamarGemini(GeminiEntradaDTO entrada) {
        long inicio = System.currentTimeMillis();

        try {
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(
                        Content.builder()
                            .parts(Arrays.asList(Part.builder().text(SYSTEM_PROMPT).build()))
                            .build()
                    )
                    .temperature(entrada.getTemperatura() != null ? entrada.getTemperatura().floatValue() : 0.7f)
                    .maxOutputTokens(entrada.getMaxTokens() != null ? entrada.getMaxTokens() : 800)
                    .tools(Arrays.asList(
                        Tool.builder()
                            .functionDeclarations(Arrays.asList(
                                criarToolBuscarCliente(),
                                criarToolAgendarAtendimento(),
                                criarToolVerificarHorarios(),
                                criarToolBuscarAtendimento()
                            ))
                            .build()
                    ))
                    .build();

            GenerateContentResponse response = client.models.generateContent(MODELO, entrada.getMensagem(), config);

            // Captura functionCalls com verificação de segurança contra exceções da SDK
            List<FunctionCall> functionCalls = null;
            try {
                functionCalls = response.functionCalls();
            } catch (Exception e) {
                functionCalls = null;
            }

            if (functionCalls != null && !functionCalls.isEmpty()) {
                FunctionCall functionCall = functionCalls.get(0);
                String nomeFuncao = functionCall.name().orElse("");
                Map<String, Object> argumentos = functionCall.args().orElse(new HashMap<>());

                System.out.println("🤖 Gemini solicitou execução da Tool: " + nomeFuncao + " com args: " + argumentos);

                Object resultado = executarFuncao(nomeFuncao, argumentos);

                String respostaFinal = enviarResultadoParaIA(entrada.getMensagem(), functionCall, resultado, config);

                GeminiSaidaDTO saida = new GeminiSaidaDTO();
                saida.setResposta(respostaFinal);
                saida.setModeloUsado(MODELO);
                saida.setTempoProcessamento(System.currentTimeMillis() - inicio);
                saida.setAcaoRealizada(nomeFuncao);
                return saida;
            }

            // Resposta normal em texto quando nenhuma Tool é acionada
            GeminiSaidaDTO saida = new GeminiSaidaDTO();
            saida.setResposta(response.text());
            saida.setModeloUsado(MODELO);
            saida.setTempoProcessamento(System.currentTimeMillis() - inicio);
            saida.setAcaoRealizada("nenhuma");
            return saida;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao processar chamada no Gemini: " + e.getMessage(), e);
        }
    }

    // ================================================================
    // DECLARAÇÃO DAS TOOLS / FUNCTIONS
    // ================================================================

    private FunctionDeclaration criarToolBuscarCliente() {
        Map<String, Schema> properties = new HashMap<>();
        properties.put("nomeCliente", Schema.builder().type("STRING").description("Nome do Cliente").build());
        properties.put("nroCliente", Schema.builder().type("INTEGER").description("ID do cliente").build());

        return FunctionDeclaration.builder()
                .name("buscarCliente")
                .description("Busca informações de um Cliente pelo nome ou ID")
                .parameters(Schema.builder().type("OBJECT").properties(properties).build())
                .build();
    }

    private FunctionDeclaration criarToolAgendarAtendimento() {
        Map<String, Schema> properties = new HashMap<>();
        properties.put("nroAnimal", Schema.builder().type("INTEGER").description("ID do animal").build());
        properties.put("nroVeterinario", Schema.builder().type("INTEGER").description("ID do veterinário").build());
        properties.put("ini_dataAtendimento", Schema.builder().type("STRING").description("Data e Hora ISO ex: 2026-08-18T10:00:00").build());
        properties.put("nroTipoAtendimento", Schema.builder().type("INTEGER").description("ID do tipo de atendimento").build());
        properties.put("observacoes", Schema.builder().type("STRING").description("Observações da consulta").build());

        return FunctionDeclaration.builder()
                .name("agendarAtendimento")
                .description("Agenda um atendimento/consulta para um animal")
                .parameters(Schema.builder()
                        .type("OBJECT")
                        .properties(properties)
                        .required(Arrays.asList("nroAnimal", "nroVeterinario", "ini_dataAtendimento", "nroTipoAtendimento"))
                        .build())
                .build();
    }

    private FunctionDeclaration criarToolVerificarHorarios() {
        Map<String, Schema> properties = new HashMap<>();
        properties.put("nroVeterinario", Schema.builder().type("INTEGER").description("ID do veterinário").build());

        return FunctionDeclaration.builder()
                .name("verificarHorarios")
                .description("Verifica horários livres e disponíveis de um veterinário")
                .parameters(Schema.builder()
                        .type("OBJECT")
                        .properties(properties)
                        .required(Arrays.asList("nroVeterinario"))
                        .build())
                .build();
    }

    private FunctionDeclaration criarToolBuscarAtendimento() {
        Map<String, Schema> properties = new HashMap<>();
        properties.put("nomeCliente", Schema.builder().type("STRING").description("Nome do cliente").build());
        properties.put("nroAnimal", Schema.builder().type("INTEGER").description("ID do animal").build());
        properties.put("nroTipoAtendimento", Schema.builder().type("INTEGER").description("ID do tipo de atendimento").build());

        return FunctionDeclaration.builder()
                .name("buscarAtendimento")
                .description("Busca histórico de atendimentos gravados")
                .parameters(Schema.builder().type("OBJECT").properties(properties).build())
                .build();
    }

    // ================================================================
    // RETORNO DO RESULTADO DA TOOL PARA A IA SINTETIZAR
    // ================================================================

    private String enviarResultadoParaIA(String promptUsuario, FunctionCall functionCall, Object resultado, GenerateContentConfig config) {
        try {
            // Converte o retorno do Hibernate/Service em JSON String simples para evitar estouro de recursão
            String resultadoJson = objectMapper.writeValueAsString(resultado);

            Content userMsg = Content.builder()
                    .role("user")
                    .parts(Arrays.asList(Part.builder().text(promptUsuario).build()))
                    .build();

            Content modelMsg = Content.builder()
                    .role("model")
                    .parts(Arrays.asList(
                        Part.builder()
                            .functionCall(functionCall)
                            .build()
                    ))
                    .build();

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("result", resultadoJson);

            Content functionResponseMsg = Content.builder()
                    .role("user")
                    .parts(Arrays.asList(
                        Part.builder()
                            .functionResponse(
                                FunctionResponse.builder()
                                    .name(functionCall.name().orElse(""))
                                    .response(responseMap)
                                    .build()
                            )
                            .build()
                    ))
                    .build();

            // Faz a segunda chamada enviando o histórico com o resultado do Banco
            GenerateContentResponse responseFinal = client.models.generateContent(
                    MODELO,
                    Arrays.asList(userMsg, modelMsg, functionResponseMsg),
                    config
            );

            return responseFinal.text();
        } catch (Exception e) {
            e.printStackTrace();
            return "Consegui consultar os dados no banco de dados, mas falhei ao processar a resposta final para você: " + e.getMessage();
        }
    }

    // ================================================================
    // EXECUÇÃO DAS FUNÇÕES LOCAIS (Chamando os Services diretos)
    // ================================================================

    private Object executarFuncao(String nomeFuncao, Map<String, Object> argumentos) {
        switch (nomeFuncao) {
            case "buscarCliente":
                return executarBuscarCliente(argumentos);
            case "agendarAtendimento":
                return executarAgendarAtendimento(argumentos);
            case "verificarHorarios":
                return executarVerificarHorarios(argumentos);
            case "buscarAtendimento":
                return executarBuscarAtendimento(argumentos);
            default:
                return Map.of("erro", "Função desconhecida: " + nomeFuncao);
        }
    }

    private Object executarBuscarCliente(Map<String, Object> args) {
        try {
            String nomeCliente = args.containsKey("nomeCliente") ? args.get("nomeCliente").toString() : null;
            Integer nroCliente = args.containsKey("nroCliente") ? Integer.parseInt(args.get("nroCliente").toString()) : null;
            
            return clienteService.listarClientes(nomeCliente, nroCliente);
        } catch (Exception e) {
            return Map.of("erro", "Erro ao buscar cliente: " + e.getMessage());
        }
    }

    private Object executarAgendarAtendimento(Map<String, Object> args) {
        try {
            Atendimento vo = new Atendimento();
            vo.nroAnimal = Integer.parseInt(args.get("nroAnimal").toString());
            vo.nroVeterinario = Integer.parseInt(args.get("nroVeterinario").toString());
            vo.nroTipoAtendimento = Integer.parseInt(args.get("nroTipoAtendimento").toString());
            vo.ini_dataAtendimento = args.get("ini_dataAtendimento").toString();
            
            if (args.containsKey("observacoes")) {
                vo.observacoes = args.get("observacoes").toString();
            }

            return atendimentoService.agendarAtendimento(vo);
        } catch (Exception e) {
            return Map.of("erro", "Erro ao agendar atendimento: " + e.getMessage());
        }
    }

    private Object executarVerificarHorarios(Map<String, Object> args) {
        try {
            Integer nroVeterinario = Integer.parseInt(args.get("nroVeterinario").toString());
            List<?> horarios = veterinarioService.calcularHorariosDisponiveis(nroVeterinario);
            return horarios.stream().map(Object::toString).toList();
        } catch (Exception e) {
            return Map.of("erro", "Erro ao verificar horários: " + e.getMessage());
        }
    }

    private Object executarBuscarAtendimento(Map<String, Object> args) {
        try {
            String nomeCliente = args.containsKey("nomeCliente") ? args.get("nomeCliente").toString() : null;
            Integer nroAnimal = args.containsKey("nroAnimal") ? Integer.parseInt(args.get("nroAnimal").toString()) : null;
            Integer nroTipoAtendimento = args.containsKey("nroTipoAtendimento") ? Integer.parseInt(args.get("nroTipoAtendimento").toString()) : null;

            return atendimentoService.listarAtendimentos(nomeCliente, nroAnimal, nroTipoAtendimento);
        } catch (Exception e) {
            return Map.of("erro", "Erro ao buscar atendimento: " + e.getMessage());
        }
    }
}