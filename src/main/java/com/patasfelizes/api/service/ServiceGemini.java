package com.patasfelizes.api.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

@Service
public class ServiceGemini {

    private final Client client;
    private static final String MODELO = "gemini-flash-latest";
    private static final String API_URL = "http://localhost:8080/api";

    private static final String SYSTEM_PROMPT = """
        Você é um assistente virtual especializado de uma clínica veterinária chamada "PatasFelizes".
        
        SUA FUNÇÃO:
        - Você ajuda Clientes com dúvidas sobre consultas, Atendimentos e informações da clínica
        - Você é educado, empático e profissional
        - Você NUNCA dá diagnósticos médicos
        - Você sempre recomenda procurar um médico para qualquer sintoma
        
        REGRAS IMPORTANTES:
        1. Para informações sobre Clientes, use a função 'buscarCliente'
        2. Para agendar consultas, use a função 'agendarConsulta'
        3. Para verificar horários disponíveis, use a função 'verificarHorarios'
        4. Para informações sobre Atendimentos, use a função 'buscarAtendimento'
        5. SEMPRE confirme os dados antes de agendar algo
        
        EXEMPLOS DE RESPOSTA:
        - "Olá! Posso ajudar com informações sobre a clínica. Como posso auxiliá-lo hoje?"
        - "Entendi que você quer agendar uma consulta. Vou verificar os horários disponíveis para você."
        - "Com base nas informações, aqui estão os dados do Cliente: [dados]"
        
        Lembre-se: Você é um assistente amigável e prestativo da Clínica PatasFelizes!
        """;

    public ServiceGemini(@Value("${gemini.api.key}") String apiKey) {
        // Inicializa o cliente oficial
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String teste() {
    try {
        GenerateContentResponse response = client.models.generateContent(MODELO, "Ping?", null);
        return response.text();
    } catch (Exception e) {
        return "Erro no teste: " + e.getMessage();
    }
}

    /**
     * MÉTODO PRINCIPAL - Executa a chamada do Gemini com SDK Oficial e Function Calling
     */
    public GeminiSaidaDTO chamarGemini(GeminiEntradaDTO entrada) {
        long inicio = System.currentTimeMillis();

        try {
            // 1. Configura System Instruction, Temperatura, Tokens e Tools
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(
                        Content.builder()
                            .parts(Arrays.asList(Part.builder().text(SYSTEM_PROMPT).build()))
                            .build()
                    )
                    .temperature(entrada.getTemperatura().floatValue())
                    .maxOutputTokens(entrada.getMaxTokens())
                    .tools(Arrays.asList(
                        Tool.builder()
                            .functionDeclarations(Arrays.asList(
                                criarToolBuscarCliente(),
                                criarToolAgendarConsulta(),
                                criarToolVerificarHorarios(),
                                criarToolBuscarAtendimento()
                            ))
                            .build()
                    ))
                    .build();

            // 2. Primeira Chamada para o Gemini
            GenerateContentResponse response = client.models.generateContent(MODELO, entrada.getMensagem(), config);

            // 3. Verifica se o Gemini solicitou execução de alguma Function
            if (response.functionCalls() != null && !response.functionCalls().isEmpty()) {
                FunctionCall functionCall = response.functionCalls().get(0);
                String nomeFuncao = functionCall.name().get();
                Map<String, Object> argumentos = functionCall.args().orElse(new HashMap<>());

                System.out.println("🤖 Gemini solicitou execução da Tool: " + nomeFuncao + " com args: " + argumentos);

                // Executa a lógica da nossa API local
                Object resultado = executarFuncao(nomeFuncao, argumentos);

                // Reenvia o resultado da função para o Gemini gerar a resposta final amigável
                String respostaFinal = enviarResultadoParaIA(entrada.getMensagem(), functionCall, resultado, config);

                GeminiSaidaDTO saida = new GeminiSaidaDTO();
                saida.setResposta(respostaFinal);
                saida.setModeloUsado(MODELO);
                saida.setTempoProcessamento(System.currentTimeMillis() - inicio);
                saida.setAcaoRealizada(nomeFuncao);
                return saida;
            }

            // Caso seja uma resposta simples (sem chamada de função)
            GeminiSaidaDTO saida = new GeminiSaidaDTO();
            saida.setResposta(response.text());
            saida.setModeloUsado(MODELO);
            saida.setTempoProcessamento(System.currentTimeMillis() - inicio);
            saida.setAcaoRealizada("nenhuma");
            return saida;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar chamada no Gemini: " + e.getMessage(), e);
        }
    }

    // ================================================================
    // DECLARAÇÃO DAS TOOLS / FUNCTIONS (Usando Strings em vez de Type)
    // ================================================================

    private FunctionDeclaration criarToolBuscarCliente() {
        Map<String, Schema> properties = new HashMap<>();
        properties.put("nomeCliente", Schema.builder().type("STRING").description("Nome do Cliente").build());
        properties.put("nroCliente", Schema.builder().type("STRING").description("ID do cliente").build());

        return FunctionDeclaration.builder()
                .name("buscarCliente")
                .description("Busca informações de um Cliente pelo nome ou ID")
                .parameters(Schema.builder().type("OBJECT").properties(properties).build())
                .build();
    }

    private FunctionDeclaration criarToolAgendarConsulta() {
        Map<String, Schema> properties = new HashMap<>();
        properties.put("nroAnimal", Schema.builder().type("INTEGER").description("ID do animal").build());
        properties.put("nroVeterinario", Schema.builder().type("INTEGER").description("ID do veterinário").build());
        properties.put("data", Schema.builder().type("STRING").description("Data no formato YYYY-MM-DD").build());
        properties.put("horario", Schema.builder().type("STRING").description("Horário HH:mm:ss").build());
        properties.put("nroTipoAtendimento", Schema.builder().type("STRING").description("1 para consulta, 2 para vacina").build());

        return FunctionDeclaration.builder()
                .name("agendarConsulta")
                .description("Agenda uma consulta para um animal registrando o veterinário responsável")
                .parameters(Schema.builder()
                        .type("OBJECT")
                        .properties(properties)
                        .required(Arrays.asList("nroAnimal", "data", "horario", "nroVeterinario"))
                        .build())
                .build();
    }

    private FunctionDeclaration criarToolVerificarHorarios() {
        Map<String, Schema> properties = new HashMap<>();
        properties.put("nroVeterinario", Schema.builder().type("INTEGER").description("ID do veterinário").build());

        return FunctionDeclaration.builder()
                .name("verificarHorarios")
                .description("Verifica horários disponíveis de um veterinário")
                .parameters(Schema.builder().type("OBJECT").properties(properties).build())
                .build();
    }

    private FunctionDeclaration criarToolBuscarAtendimento() {
        Map<String, Schema> properties = new HashMap<>();
        properties.put("nroTipoAtendimento", Schema.builder().type("INTEGER").description("ID do tipo de atendimento").build());
        properties.put("nomeCliente", Schema.builder().type("STRING").description("Nome do cliente").build());
        properties.put("nroAnimal", Schema.builder().type("INTEGER").description("ID do animal").build());

        return FunctionDeclaration.builder()
                .name("buscarAtendimento")
                .description("Busca informações sobre um Atendimento médico")
                .parameters(Schema.builder().type("OBJECT").properties(properties).build())
                .build();
    }

    // ================================================================
    // RETORNO DO RESULTADO PARA A IA
    // ================================================================

    private String enviarResultadoParaIA(String promptUsuario, FunctionCall functionCall, Object resultado, GenerateContentConfig config) {
        try {
            // Reassocia o histórico completo da conversa para manter contexto:
            // 1. Pergunta do Usuário
            Content userMsg = Content.builder()
                    .role("user")
                    .parts(Arrays.asList(Part.builder().text(promptUsuario).build()))
                    .build();

            // 2. Pedido de Function Call do Gemini
            Content modelMsg = Content.builder()
                    .role("model")
                    .parts(Arrays.asList(
                        Part.builder()
                            .functionCall(functionCall)
                            .build()
                    ))
                    .build();

            // 3. Resposta da Function Call com os dados buscados da API
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("result", resultado);

            Content functionResponseMsg = Content.builder()
                    .role("user")
                    .parts(Arrays.asList(
                        Part.builder()
                            .functionResponse(
                                FunctionResponse.builder()
                                    .name(functionCall.name().get())
                                    .response(responseMap)
                                    .build()
                            )
                            .build()
                    ))
                    .build();

            GenerateContentResponse responseFinal = client.models.generateContent(
                    MODELO,
                    Arrays.asList(userMsg, modelMsg, functionResponseMsg),
                    config
            );

            return responseFinal.text();
        } catch (Exception e) {
            return "Operação realizada com sucesso no sistema, porém falhou na sintetização da resposta: " + e.getMessage();
        }
    }

    // ================================================================
    // EXECUÇÃO DAS FUNÇÕES LOCAIS (DISPATCHER)
    // ================================================================

    private Object executarFuncao(String nomeFuncao, Map<String, Object> argumentos) {
        switch (nomeFuncao) {
            case "buscarCliente":
                return executarBuscarCliente(argumentos);
            case "agendarConsulta":
                return executarAgendarConsulta(argumentos);
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
            String url = API_URL + "/clientes";
            if (args.containsKey("nroCliente")) {
                url += "?nroCliente=" + args.get("nroCliente");
            } else if (args.containsKey("nomeCliente")) {
                url += "?nomeCliente=" + args.get("nomeCliente");
            }
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForEntity(url, Object.class).getBody();
        } catch (Exception e) {
            return Map.of("erro", "Erro ao buscar cliente: " + e.getMessage());
        }
    }

    private Object executarAgendarConsulta(Map<String, Object> args) {
        try {
            String url = API_URL + "/consultas";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(args, headers);

            ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);
            return response.getBody();
        } catch (Exception e) {
            return Map.of("erro", "Erro ao agendar consulta: " + e.getMessage());
        }
    }

    private Object executarVerificarHorarios(Map<String, Object> args) {
        try {
            String url = API_URL + "/horarios?nroVeterinario=" + args.get("nroVeterinario");
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForEntity(url, Object.class).getBody();
        } catch (Exception e) {
            return Map.of("erro", "Erro ao verificar horários: " + e.getMessage());
        }
    }

    private Object executarBuscarAtendimento(Map<String, Object> args) {
        try {
            String url = API_URL + "/atendimentos";
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForEntity(url, Object.class).getBody();
        } catch (Exception e) {
            return Map.of("erro", "Erro ao buscar atendimento: " + e.getMessage());
        }
    }
}