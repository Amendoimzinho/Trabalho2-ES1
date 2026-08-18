package com.patasfelizes.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patasfelizes.api.dto.GeminiEntradaDTO;
import com.patasfelizes.api.dto.GeminiSaidaDTO;

@Service
public class ServiceGemini {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private static final String API_URL = "http://localhost:8080/api";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // 🔥 SYSTEM PROMPT - Define o papel da IA
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
    
    public ServiceGemini() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 🔥 MÉTODO PRINCIPAL - CHAMA O GEMINI COM SYSTEM PROMPT E TOOLS
     */
    public GeminiSaidaDTO chamarGemini(GeminiEntradaDTO entrada) {
        long inicio = System.currentTimeMillis();
        
        try {
            String url = GEMINI_URL + "?key=" + apiKey;
            
            // 1. Monta o corpo com system prompt + tools
            Map<String, Object> requestBody = montarCorpoCompleto(entrada);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // 2. 🔥 FAZ A CHAMADA PARA O GEMINI
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );
            
            // 3. Processa a resposta (pode ter chamada de função)
            return processarResposta(response.getBody(), inicio, entrada);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao chamar Gemini: " + e.getMessage());
        }
    }
    
    /**
     * Monta o corpo com SYSTEM PROMPT e TOOLS
     */
    private Map<String, Object> montarCorpoCompleto(GeminiEntradaDTO entrada) {
        Map<String, Object> body = new HashMap<>();
        
        // 🔥 CONTEÚDO DA CONVERSA
        List<Map<String, Object>> contents = new ArrayList<>();
        
        // 1. System prompt (instruções para a IA)
        Map<String, Object> systemContent = new HashMap<>();
        List<Map<String, String>> systemParts = new ArrayList<>();
        Map<String, String> systemPart = new HashMap<>();
        systemPart.put("text", SYSTEM_PROMPT);
        systemParts.add(systemPart);
        systemContent.put("parts", systemParts);
        systemContent.put("role", "user");
        contents.add(systemContent);
        
        // 2. Mensagem do usuário
        Map<String, Object> userContent = new HashMap<>();
        List<Map<String, String>> userParts = new ArrayList<>();
        Map<String, String> userPart = new HashMap<>();
        userPart.put("text", entrada.getMensagem());
        userParts.add(userPart);
        userContent.put("parts", userParts);
        userContent.put("role", "user");
        contents.add(userContent);
        
        body.put("contents", contents);
        
        // 🔥 TOOLS (funções que a IA pode chamar)
        Map<String, Object> tools = new HashMap<>();
        List<Map<String, Object>> functionDeclarations = new ArrayList<>();
        
        // Tool 1: Buscar Cliente
        functionDeclarations.add(criarToolBuscarCliente());
        
        // Tool 2: Agendar consulta
        functionDeclarations.add(criarToolAgendarConsulta());
        
        // Tool 3: Verificar horários
        functionDeclarations.add(criarToolVerificarHorarios());
        
        // Tool 4: Buscar Atendimento
        functionDeclarations.add(criarToolBuscarAtendimento());
        
        tools.put("functionDeclarations", functionDeclarations);
        body.put("tools", tools);
        
        // Configurações
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", entrada.getTemperatura());
        generationConfig.put("maxOutputTokens", entrada.getMaxTokens());
        body.put("generationConfig", generationConfig);
        
        return body;
    }
    
    /**
     * 🔧 TOOL 1: Buscar Cliente
     */
    private Map<String, Object> criarToolBuscarCliente() {
        Map<String, Object> function = new HashMap<>();
        function.put("name", "buscarCliente");
        function.put("description", "Busca informações de um Cliente pelo nome ou ID");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> nomeClienteProp = new HashMap<>();
        nomeClienteProp.put("type", "string");
        nomeClienteProp.put("description", "Nome completo ou parcial do Cliente");
        properties.put("nomeCliente", nomeClienteProp);
        
        Map<String, Object> nroClienteProp = new HashMap<>();
        nroClienteProp.put("type", "string");
        nroClienteProp.put("description", "ID do cliente no sistema (número de indentificação)");
        properties.put("nroCliente", nroClienteProp);

        parameters.put("properties", properties);
        function.put("parameters", parameters);
        
        return function;
    }
    
    /**
     * 🔧 TOOL 2: Agendar consulta
     */
    private Map<String, Object> criarToolAgendarConsulta() {
        Map<String, Object> function = new HashMap<>();
        function.put("name", "agendarConsulta");
        function.put("description", "Agenda uma consulta para um animal, registrando o veterinario responsavel e o dono do animal");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> nroAnimalProp = new HashMap<>();
        nroAnimalProp.put("type", "integer");
        nroAnimalProp.put("description", "ID do animal que será atendido (número de indentificação)");
        properties.put("nroAnimal", nroAnimalProp);
        
        Map<String, Object> nroVeterinarioProp = new HashMap<>();
        nroVeterinarioProp.put("type", "integer");
        nroVeterinarioProp.put("description", "ID do veterinário responsável pelo atendimento (número de indentificação)");
        properties.put("nroVeterinario", nroVeterinarioProp);
        
        Map<String, Object> dataProp = new HashMap<>();
        dataProp.put("type", "string");
        dataProp.put("description", "Data do atendimento no formato YYYY-MM-DD");
        properties.put("data", dataProp);
        
        Map<String, Object> horarioProp = new HashMap<>();
        horarioProp.put("type", "string");
        horarioProp.put("description", "Horário do atendimento no formato HH:mm:ss)");
        properties.put("horario", horarioProp);
        
        Map<String, Object> nroTipoAtendimentoProp = new HashMap<>();
        nroTipoAtendimentoProp.put("type", "string");
        nroTipoAtendimentoProp.put("description", "Numero do tipo de atendimento, 1 para consulta e 2 para vacinação");
        properties.put("nroTipoAtendimento", nroTipoAtendimentoProp);
        
        parameters.put("properties", properties);
        List<String> required = Arrays.asList("nroAnimal", "data", "horario", "nroVeterinario");
        parameters.put("required", required);
        
        function.put("parameters", parameters);
        
        return function;
    }
    
    /**
     * 🔧 TOOL 3: Verificar horários disponíveis
     */
    private Map<String, Object> criarToolVerificarHorarios() {
        Map<String, Object> function = new HashMap<>();
        function.put("name", "verificarHorarios");
        function.put("description", "Verifica horários disponíveis de um veterinário");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> nroVeterinarioProp = new HashMap<>();
        nroVeterinarioProp.put("type", "integer");
        nroVeterinarioProp.put("description", "ID do veterinario que terá seus horários consultados (número de indentificação)");
        properties.put("nroVeterinario", nroVeterinarioProp);
        
        parameters.put("properties", properties);
        function.put("parameters", parameters);
        
        return function;
    }
    
    /**
     * 🔧 TOOL 4: Buscar Atendimento
     */
    private Map<String, Object> criarToolBuscarAtendimento() {
        Map<String, Object> function = new HashMap<>();
        function.put("name", "buscarAtendimento");
        function.put("description", "Busca informações sobre um Atendimento médico");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> nroTipoAtendimentoProp = new HashMap<>();
        nroTipoAtendimentoProp.put("type", "integer");
        nroTipoAtendimentoProp.put("description", "ID do tipo de atendimento (número de indentificação)");
        properties.put("nroTipoAtendimento", nroTipoAtendimentoProp);
        
        Map<String, Object> nomeClienteProp = new HashMap<>();
        nomeClienteProp.put("type", "string");
        nomeClienteProp.put("description", "nome do cliente(Dono do animal)");
        properties.put("nomeCliente", nomeClienteProp);

        Map<String, Object> nroAnimalProp = new HashMap<>();
        nroAnimalProp.put("type", "integer");
        nroAnimalProp.put("description", "ID do animal no sistema (número de indentificação)");
        properties.put("nroAnimal", nroAnimalProp);
        
        parameters.put("properties", properties);
        function.put("parameters", parameters);
        
        return function;
    }
    
    /**
     *  PROCESSA A RESPOSTA E EXECUTA AS FUNÇÕES
     */
    /**
 * Processa a resposta do Gemini, identificando se há chamada de função.
 * 
 * @param responseBody Resposta JSON do Gemini
 * @param inicio Timestamp do início
 * @param entradaOriginal Mensagem original do usuário
 * @return DTO com a resposta final
 */
private GeminiSaidaDTO processarResposta(
        String responseBody, 
        long inicio, 
        GeminiEntradaDTO entradaOriginal) {
    
    try {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode firstPart = getFirstPart(root);
        
        // Verifica se é uma chamada de função
        JsonNode functionCall = firstPart.path("functionCall");
        
        if (functionCall.isMissingNode()) {
            // Resposta direta
            return criarSaidaDireta(root, firstPart, inicio);
        } else {
            // Resposta com função
            return processarComFuncao(root, functionCall, inicio, entradaOriginal);
        }
        
    } catch (Exception e) {
        throw new RuntimeException("Erro ao processar resposta: " + e.getMessage(), e);
    }
}

/**
 * Extrai a primeira parte da resposta.
 */
private JsonNode getFirstPart(JsonNode root) {
    JsonNode candidates = root.path("candidates");
    
    if (!candidates.isArray() || candidates.size() == 0) {
        throw new RuntimeException("Nenhum candidato na resposta");
    }
    
    JsonNode content = candidates.get(0).path("content");
    JsonNode parts = content.path("parts");
    
    if (!parts.isArray() || parts.size() == 0) {
        throw new RuntimeException("Nenhuma parte na resposta");
    }
    
    return parts.get(0);
}

/**
 * Cria resposta para caso sem função.
 */
private GeminiSaidaDTO criarSaidaDireta(JsonNode root, JsonNode firstPart, long inicio) {
    GeminiSaidaDTO saida = new GeminiSaidaDTO();
    saida.setResposta(firstPart.path("text").asText());
    saida.setModeloUsado(root.path("modelVersion").asText("gemini-pro"));
    saida.setTempoProcessamento(System.currentTimeMillis() - inicio);
    saida.setAcaoRealizada("nenhuma");
    return saida;
}

/**
 * Processa resposta com chamada de função.
 */
private GeminiSaidaDTO processarComFuncao(
        JsonNode root, 
        JsonNode functionCall, 
        long inicio, 
        GeminiEntradaDTO entradaOriginal) {
    
    // Extrai dados da função
    String nomeFuncao = functionCall.path("name").asText();
    Map<String, Object> argumentos = objectMapper.convertValue(
        functionCall.path("args"), 
        Map.class
    );
    
    System.out.println("Executando: " + nomeFuncao + " com " + argumentos);
    
    // Executa e envia resultado
    Object resultado = executarFuncao(nomeFuncao, argumentos);
    String respostaFinal = enviarResultadoParaIA(nomeFuncao, argumentos, resultado, entradaOriginal);
    
    // Monta resposta
    GeminiSaidaDTO saida = new GeminiSaidaDTO();
    saida.setResposta(respostaFinal);
    saida.setModeloUsado(root.path("modelVersion").asText("gemini-pro"));
    saida.setTempoProcessamento(System.currentTimeMillis() - inicio);
    saida.setAcaoRealizada(nomeFuncao);
    
    return saida;
}
    
    /**
 * Executa a função solicitada pela IA.
 * 
 * <p>Este método atua como um dispatcher que direciona a chamada para a
 * implementação específica de cada função baseado no nome recebido.</p>
 * 
 * @param nomeFuncao Nome da função a ser executada
 * @param argumentos Argumentos da função (extraídos do functionCall do Gemini)
 * @return Resultado da execução da função (pode ser Map, List, ou objeto)
 */
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
            return Map.of(
                "erro", "Função desconhecida: " + nomeFuncao,
                "mensagem", "A função solicitada não está disponível no sistema"
            );
    }
}

// ================================================================
// IMPLEMENTAÇÕES DAS FUNÇÕES
// ================================================================

/**
 * Busca um cliente pelo ID, CPF ou nome.
 * 
 * <p>Prioridade de busca: ID > CPF > Nome</p>
 * 
 * @param args Map com os argumentos da busca
 * @return Dados do cliente encontrado ou mensagem de erro
 */
private Object executarBuscarCliente(Map<String, Object> args) {
    try {
        String url = API_URL + "/clientes";
        
        // Constrói a URL baseado no critério informado
        if (args.containsKey("nroCliente")) {
            url += "?nroCliente=" + args.get("nroCliente");
        } else if (args.containsKey("nomeCliente")) {
            url += "?nomeCliente=" + args.get("nomeCliente");
        }
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        
        return response.getBody();
        
    } catch (Exception e) {
        return Map.of(
            "erro", "ERRO_BUSCAR_CLIENTE",
            "mensagem", "Erro ao buscar cliente: " + e.getMessage()
        );
    }
}

/**
 * Agenda uma consulta para um cliente.
 * 
 * <p>Validações realizadas pelo endpoint de agendamento:</p>
 * <ul>
 *   <li>Cliente existe</li>
 *   <li>Médico existe</li>
 *   <li>Horário disponível</li>
 * </ul>
 * 
 * @param args Map com os dados do agendamento
 * @return Dados da consulta agendada ou mensagem de erro
 */
private Object executarAgendarConsulta(Map<String, Object> args) {
    try {
        // Verifica se os dados obrigatórios foram enviados
        if (!args.containsKey("nroAnimal")||!args.containsKey("data")) {
            return Map.of(
                "erro", "NRO_ANIMAL_NAO_INFORMADO",
                "mensagem", "Informe o ID do animal para agendar a consulta"
            );
        }
        
        if (!args.containsKey("data") || !args.containsKey("horario")) {
            return Map.of(
                "erro", "DADOS_INCOMPLETOS",
                "mensagem", "Informe data e horário para o agendamento"
            );
        }

        if (!args.containsKey("nroVeterinario")) {
            return Map.of(
                "erro", "NRO_VETERINARIO_NAO_INFORMADO",
                "mensagem", "Informe o ID do veterinario para agendar a consulta"
            );
        }
        
        String url = API_URL + "/consultas";
        
        Map<String, Object> body = new HashMap<>();
        body.put("nroAnimal", args.get("nroAnimal"));
        body.put("nroVeterinario", args.get("nroVeterinario"));
        body.put("data", args.get("data"));
        body.put("horario", args.get("horario"));
        body.put("nroTipoAtendimento", args.get("nroTipoAtendimento"));
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        
        ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);
        
        return response.getBody();
        
    } catch (Exception e) {
        return Map.of(
            "erro", "ERRO_AGENDAR_CONSULTA",
            "mensagem", "Erro ao agendar consulta: " + e.getMessage()
        );
    }
}

/**
 * Verifica horários disponíveis para uma especialidade em uma data específica.
 * 
 * @param args Map com especialidade e data
 * @return Lista de horários disponíveis ou mensagem de erro
 */
private Object executarVerificarHorarios(Map<String, Object> args) {
    try {
        String especialidade = (String) args.get("especialidade");
        String data = (String) args.get("data");
        
        if (especialidade == null || especialidade.trim().isEmpty()) {
            return Map.of(
                "erro", "ESPECIALIDADE_NAO_INFORMADA",
                "mensagem", "Informe a especialidade para verificar horários"
            );
        }
        
        if (data == null || data.trim().isEmpty()) {
            return Map.of(
                "erro", "DATA_NAO_INFORMADA",
                "mensagem", "Informe a data para verificar horários"
            );
        }
        
        String url = String.format(
            API_URL + "/horarios?especialidade=%s&data=%s",
            especialidade, data
        );
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        
        return response.getBody();
        
    } catch (Exception e) {
        return Map.of(
            "erro", "ERRO_VERIFICAR_HORARIOS",
            "mensagem", "Erro ao verificar horários: " + e.getMessage()
        );
    }
}

/**
 * Busca informações de um atendimento pelo código ou nome.
 * 
 * <p>Prioridade: código > nome</p>
 * 
 * @param args Map com código ou nome do atendimento
 * @return Dados do atendimento ou mensagem de erro
 */
private Object executarBuscarAtendimento(Map<String, Object> args) {
    try {
        String url = API_URL + "/atendimentos";
        
        if (args.containsKey("codigo")) {
            url += "/" + args.get("codigo");
        } else if (args.containsKey("nome")) {
            url += "?nome=" + args.get("nome");
        } else {
            return Map.of(
                "erro", "CRITERIO_NAO_INFORMADO",
                "mensagem", "Informe código ou nome para buscar o atendimento"
            );
        }
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        
        return response.getBody();
        
    } catch (Exception e) {
        return Map.of(
            "erro", "ERRO_BUSCAR_ATENDIMENTO",
            "mensagem", "Erro ao buscar atendimento: " + e.getMessage()
        );
    }
}
    
    /**
     * 🔥 ENVIA O RESULTADO DE VOLTA PARA A IA
     */
    private String enviarResultadoParaIA(String nomeFuncao, Map<String, Object> argumentos, 
                                         Object resultado, GeminiEntradaDTO entradaOriginal) {
        try {
            String url = GEMINI_URL + "?key=" + apiKey;
            
            // Monta a conversa completa
            List<Map<String, Object>> contents = new ArrayList<>();
            
            // 1. System prompt
            Map<String, Object> systemContent = new HashMap<>();
            List<Map<String, String>> systemParts = new ArrayList<>();
            Map<String, String> systemPart = new HashMap<>();
            systemPart.put("text", SYSTEM_PROMPT);
            systemParts.add(systemPart);
            systemContent.put("parts", systemParts);
            systemContent.put("role", "user");
            contents.add(systemContent);
            
            // 2. Mensagem do usuário
            Map<String, Object> userContent = new HashMap<>();
            List<Map<String, String>> userParts = new ArrayList<>();
            Map<String, String> userPart = new HashMap<>();
            userPart.put("text", entradaOriginal.getMensagem());
            userParts.add(userPart);
            userContent.put("parts", userParts);
            userContent.put("role", "user");
            contents.add(userContent);
            
            // 3. Resposta da IA (com function call)
            Map<String, Object> modelContent = new HashMap<>();
            List<Map<String, Object>> modelParts = new ArrayList<>();
            Map<String, Object> modelPart = new HashMap<>();
            Map<String, Object> functionCall = new HashMap<>();
            functionCall.put("name", nomeFuncao);
            functionCall.put("args", argumentos);
            modelPart.put("functionCall", functionCall);
            modelParts.add(modelPart);
            modelContent.put("parts", modelParts);
            modelContent.put("role", "model");
            contents.add(modelContent);
            
            // 4. Resultado da função
            Map<String, Object> functionContent = new HashMap<>();
            List<Map<String, Object>> functionParts = new ArrayList<>();
            Map<String, Object> functionPart = new HashMap<>();
            Map<String, Object> functionResponse = new HashMap<>();
            functionResponse.put("name", nomeFuncao);
            functionResponse.put("response", resultado);
            functionPart.put("functionResponse", functionResponse);
            functionParts.add(functionPart);
            functionContent.put("parts", functionParts);
            functionContent.put("role", "user");
            contents.add(functionContent);
            
            Map<String, Object> body = new HashMap<>();
            body.put("contents", contents);
            
            // 🔥 FAZ A CHAMADA FINAL
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );
            
            // Pega a resposta final
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText("Resposta gerada com sucesso!");
                }
            }
            
            return "Operação realizada com sucesso! " + resultado;
            
        } catch (Exception e) {
            return "Erro ao processar resultado: " + e.getMessage() + ". Dados: " + resultado;
        }
    }
}
