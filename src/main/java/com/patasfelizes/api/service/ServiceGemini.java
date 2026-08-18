package com.patasfelizes.api.service;

import com.patasfelizes.api.dto.GeminiEntradaDTO;
import com.patasfelizes.api.dto.GeminiSaidaDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class ServiceGemini {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // 🔥 SYSTEM PROMPT - Define o papel da IA
    private static final String SYSTEM_PROMPT = """
        Você é um assistente virtual especializado de uma clínica veterinária chamada "PatasFelizes".
        
        SUA FUNÇÃO:
        - Você ajuda pacientes com dúvidas sobre consultas, exames e informações da clínica
        - Você é educado, empático e profissional
        - Você NUNCA dá diagnósticos médicos
        - Você sempre recomenda procurar um médico para qualquer sintoma
        
        REGRAS IMPORTANTES:
        1. Para informações sobre pacientes, use a função 'buscarPaciente'
        2. Para agendar consultas, use a função 'agendarConsulta'
        3. Para verificar horários disponíveis, use a função 'verificarHorarios'
        4. Para informações sobre exames, use a função 'buscarExame'
        5. SEMPRE confirme os dados antes de agendar algo
        
        EXEMPLOS DE RESPOSTA:
        - "Olá! Posso ajudar com informações sobre a clínica. Como posso auxiliá-lo hoje?"
        - "Entendi que você quer agendar uma consulta. Vou verificar os horários disponíveis para você."
        - "Com base nas informações, aqui estão os dados do paciente: [dados]"
        
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
        
        // Tool 1: Buscar paciente
        functionDeclarations.add(criarToolBuscarPaciente());
        
        // Tool 2: Agendar consulta
        functionDeclarations.add(criarToolAgendarConsulta());
        
        // Tool 3: Verificar horários
        functionDeclarations.add(criarToolVerificarHorarios());
        
        // Tool 4: Buscar exame
        functionDeclarations.add(criarToolBuscarExame());
        
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
     * 🔧 TOOL 1: Buscar paciente
     */
    private Map<String, Object> criarToolBuscarPaciente() {
        Map<String, Object> function = new HashMap<>();
        function.put("name", "buscarPaciente");
        function.put("description", "Busca informações de um paciente pelo nome, CPF ou ID");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> nomeProp = new HashMap<>();
        nomeProp.put("type", "string");
        nomeProp.put("description", "Nome completo ou parcial do paciente");
        properties.put("nome", nomeProp);
        
        Map<String, Object> cpfProp = new HashMap<>();
        cpfProp.put("type", "string");
        cpfProp.put("description", "CPF do paciente (apenas números)");
        properties.put("cpf", cpfProp);
        
        Map<String, Object> idProp = new HashMap<>();
        idProp.put("type", "integer");
        idProp.put("description", "ID do paciente no sistema");
        properties.put("id", idProp);

        Map<String, Object> enderecoProp = new HashMap<>();
        enderecoProp.put("type", "object")
        enderecoProp.put("description", "Informacoes do endereco do Cliente, como logradouro, bairro, cidade, estado e CEP");
        enderecoProp.ut
        
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
        function.put("description", "Agenda uma consulta para um paciente com um médico");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> pacienteIdProp = new HashMap<>();
        pacienteIdProp.put("type", "integer");
        pacienteIdProp.put("description", "ID do paciente");
        properties.put("pacienteId", pacienteIdProp);
        
        Map<String, Object> medicoIdProp = new HashMap<>();
        medicoIdProp.put("type", "integer");
        medicoIdProp.put("description", "ID do médico");
        properties.put("medicoId", medicoIdProp);
        
        Map<String, Object> dataProp = new HashMap<>();
        dataProp.put("type", "string");
        dataProp.put("description", "Data da consulta no formato YYYY-MM-DD");
        properties.put("data", dataProp);
        
        Map<String, Object> horarioProp = new HashMap<>();
        horarioProp.put("type", "string");
        horarioProp.put("description", "Horário da consulta (ex: 14:30)");
        properties.put("horario", horarioProp);
        
        Map<String, Object> especialidadeProp = new HashMap<>();
        especialidadeProp.put("type", "string");
        especialidadeProp.put("description", "Especialidade médica (ex: Cardiologia)");
        properties.put("especialidade", especialidadeProp);
        
        parameters.put("properties", properties);
        List<String> required = Arrays.asList("pacienteId", "data", "horario", "especialidade");
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
        function.put("description", "Verifica horários disponíveis para uma especialidade em uma data");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> especialidadeProp = new HashMap<>();
        especialidadeProp.put("type", "string");
        especialidadeProp.put("description", "Especialidade médica (ex: Dermatologia)");
        properties.put("especialidade", especialidadeProp);
        
        Map<String, Object> dataProp = new HashMap<>();
        dataProp.put("type", "string");
        dataProp.put("description", "Data para verificar no formato YYYY-MM-DD");
        properties.put("data", dataProp);
        
        parameters.put("properties", properties);
        List<String> required = Arrays.asList("especialidade", "data");
        parameters.put("required", required);
        
        function.put("parameters", parameters);
        
        return function;
    }
    
    /**
     * 🔧 TOOL 4: Buscar exame
     */
    private Map<String, Object> criarToolBuscarExame() {
        Map<String, Object> function = new HashMap<>();
        function.put("name", "buscarExame");
        function.put("description", "Busca informações sobre um exame médico");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> nomeExameProp = new HashMap<>();
        nomeExameProp.put("type", "string");
        nomeExameProp.put("description", "Nome do exame (ex: Hemograma)");
        properties.put("nomeExame", nomeExameProp);
        
        Map<String, Object> codigoProp = new HashMap<>();
        codigoProp.put("type", "string");
        codigoProp.put("description", "Código do exame (ex: EXM-123)");
        properties.put("codigo", codigoProp);
        
        parameters.put("properties", properties);
        function.put("parameters", parameters);
        
        return function;
    }
    
    /**
     * 🔥 PROCESSA A RESPOSTA E EXECUTA AS FUNÇÕES
     */
    private GeminiSaidaDTO processarResposta(String responseBody, long inicio, GeminiEntradaDTO entradaOriginal) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.path("content");
                JsonNode parts = content.path("parts");
                
                if (parts.isArray() && parts.size() > 0) {
                    JsonNode firstPart = parts.get(0);
                    JsonNode functionCall = firstPart.path("functionCall");
                    
                    // 🔥 SE A IA QUER CHAMAR UMA FUNÇÃO
                    if (!functionCall.isMissingNode()) {
                        String nomeFuncao = functionCall.path("name").asText();
                        JsonNode args = functionCall.path("args");
                        Map<String, Object> argumentos = objectMapper.convertValue(args, Map.class);
                        
                        System.out.println("🤖 IA quer executar: " + nomeFuncao);
                        System.out.println("📝 Com argumentos: " + argumentos);
                        
                        // 🔥 EXECUTA A FUNÇÃO (CHAMA ENDPOINT INTERNO)
                        Object resultado = executarFuncao(nomeFuncao, argumentos);
                        
                        System.out.println("✅ Resultado da função: " + resultado);
                        
                        // 🔥 ENVIA RESULTADO DE VOLTA PARA IA
                        String respostaFinal = enviarResultadoParaIA(
                            nomeFuncao, 
                            argumentos, 
                            resultado, 
                            entradaOriginal
                        );
                        
                        GeminiSaidaDTO saida = new GeminiSaidaDTO();
                        saida.setResposta(respostaFinal);
                        saida.setModeloUsado(root.path("modelVersion").asText("gemini-pro"));
                        saida.setTempoProcessamento(System.currentTimeMillis() - inicio);
                        saida.setAcaoRealizada(nomeFuncao);
                        
                        return saida;
                    }
                    
                    // Se não tem função, é resposta normal
                    String texto = firstPart.path("text").asText();
                    GeminiSaidaDTO saida = new GeminiSaidaDTO();
                    saida.setResposta(texto);
                    saida.setModeloUsado(root.path("modelVersion").asText("gemini-pro"));
                    saida.setTempoProcessamento(System.currentTimeMillis() - inicio);
                    saida.setAcaoRealizada("nenhuma");
                    
                    return saida;
                }
            }
            
            throw new RuntimeException("Resposta inválida do Gemini");
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta: " + e.getMessage());
        }
    }
    
    /**
     * 🔥 EXECUTA A FUNÇÃO - AQUI VOCÊ CHAMA SEUS ENDPOINTS INTERNOS
     */
    private Object executarFuncao(String nomeFuncao, Map<String, Object> argumentos) {
        switch (nomeFuncao) {
            case "buscarPaciente":
                return executarBuscarPaciente(argumentos);
                
            case "agendarConsulta":
                return executarAgendarConsulta(argumentos);
                
            case "verificarHorarios":
                return executarVerificarHorarios(argumentos);
                
            case "buscarExame":
                return executarBuscarExame(argumentos);
                
            default:
                return Map.of("erro", "Função desconhecida: " + nomeFuncao);
        }
    }
    
    /**
     * 🔥 IMPLEMENTAÇÕES DAS FUNÇÕES - CHAMAM OS ENDPOINTS DA SUA API
     */
    
    private Object executarBuscarPaciente(Map<String, Object> args) {
        try {
            // 🔥 AQUI VOCÊ CHAMA SEU ENDPOINT REAL
            String url = "http://localhost:8080/api/pacientes/buscar";
            
            List<String> params = new ArrayList<>();
            if (args.containsKey("id")) {
                url += "/" + args.get("id");
            } else if (args.containsKey("cpf")) {
                url += "?cpf=" + args.get("cpf");
            } else if (args.containsKey("nome")) {
                url += "?nome=" + args.get("nome");
            }
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            
            return response.getBody();
            
        } catch (Exception e) {
            return Map.of("erro", "Erro ao buscar paciente: " + e.getMessage());
        }
    }
    
    private Object executarAgendarConsulta(Map<String, Object> args) {
        try {
            // 🔥 CHAMA SEU ENDPOINT DE AGENDAMENTO
            String url = "http://localhost:8080/api/consultas";
            
            Map<String, Object> body = new HashMap<>();
            body.put("pacienteId", args.get("pacienteId"));
            body.put("medicoId", args.get("medicoId"));
            body.put("data", args.get("data"));
            body.put("horario", args.get("horario"));
            body.put("especialidade", args.get("especialidade"));
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);
            
            return response.getBody();
            
        } catch (Exception e) {
            return Map.of("erro", "Erro ao agendar consulta: " + e.getMessage());
        }
    }
    
    private Object executarVerificarHorarios(Map<String, Object> args) {
        try {
            // 🔥 CHAMA SEU ENDPOINT DE HORÁRIOS
            String especialidade = (String) args.get("especialidade");
            String data = (String) args.get("data");
            
            String url = String.format(
                "http://localhost:8080/api/horarios?especialidade=%s&data=%s",
                especialidade, data
            );
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            
            return response.getBody();
            
        } catch (Exception e) {
            return Map.of("erro", "Erro ao verificar horários: " + e.getMessage());
        }
    }
    
    private Object executarBuscarExame(Map<String, Object> args) {
        try {
            // 🔥 CHAMA SEU ENDPOINT DE EXAMES
            String url = "http://localhost:8080/api/exames";
            
            if (args.containsKey("codigo")) {
                url += "/" + args.get("codigo");
            } else if (args.containsKey("nomeExame")) {
                url += "?nome=" + args.get("nomeExame");
            }
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            
            return response.getBody();
            
        } catch (Exception e) {
            return Map.of("erro", "Erro ao buscar exame: " + e.getMessage());
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
