package edu.amrita.medical_app.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiAIService {

    @Value("${google.ai.api.key}")
    private String apiKey;

    @Value("${google.ai.model}")
    private String model;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiAIService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public String generateHealthResponse(String userMessage, List<String> conversationHistory) {
        try {
            // Build the prompt with medical context
            String systemPrompt = buildMedicalSystemPrompt();
            String fullPrompt = systemPrompt + "\n\nUser: " + userMessage;

            // Create request body for Gemini API
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", fullPrompt);
            contents.put("parts", List.of(parts));
            requestBody.put("contents", List.of(contents));

            // Add generation config for better medical responses
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.3);
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 1024);
            requestBody.put("generationConfig", generationConfig);

            // Make API call to Gemini
            String response = webClient.post()
                .uri("/v1/models/" + model + ":generateContent?key=" + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            // Parse response
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode candidates = jsonResponse.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).get("content");
                if (content != null) {
                    JsonNode responseParts = content.get("parts");
                    if (responseParts != null && responseParts.isArray() && responseParts.size() > 0) {
                        JsonNode text = responseParts.get(0).get("text");
                        if (text != null) {
                            return text.asText();
                        }
                    }
                }
            }

            return "I'm sorry, I couldn't process your request at the moment. Please try again.";

        } catch (Exception e) {
            e.printStackTrace();
            return "I'm experiencing technical difficulties. Please consult with a healthcare professional for medical advice.";
        }
    }

    public String analyzeHealthSymptoms(List<String> conversationHistory) {
        try {
            String analysisPrompt = buildHealthAnalysisPrompt(conversationHistory);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", analysisPrompt);
            contents.put("parts", List.of(parts));
            requestBody.put("contents", List.of(contents));

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.2);
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 1024);
            requestBody.put("generationConfig", generationConfig);

            String response = webClient.post()
                .uri("/v1/models/" + model + ":generateContent?key=" + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode candidates = jsonResponse.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).get("content");
                if (content != null) {
                    JsonNode analysisParts = content.get("parts");
                    if (analysisParts != null && analysisParts.isArray() && analysisParts.size() > 0) {
                        JsonNode text = analysisParts.get(0).get("text");
                        if (text != null) {
                            return text.asText();
                        }
                    }
                }
            }

            return "Unable to analyze symptoms at this time.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Analysis unavailable. Please consult a healthcare professional.";
        }
    }

    private String buildMedicalSystemPrompt() {
        return """
            You are HealthConnect AI, a helpful medical assistant chatbot. Your role is to:
            
            1. Provide general health information and wellness tips
            2. Help users understand common symptoms and conditions
            3. Suggest when to seek professional medical care
            4. Offer lifestyle and preventive health advice
            
            IMPORTANT DISCLAIMERS:
            - You are NOT a replacement for professional medical advice
            - Always recommend consulting healthcare professionals for serious concerns
            - Do not provide specific diagnoses or treatment recommendations
            - Encourage users to seek emergency care for urgent symptoms
            
            Be empathetic, informative, and always prioritize user safety. If symptoms seem serious, 
            strongly recommend immediate medical attention.
            """;
    }

    private String buildHealthAnalysisPrompt(List<String> conversationHistory) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the following health conversation and provide a structured assessment:\n\n");
        
        for (String message : conversationHistory) {
            prompt.append(message).append("\n");
        }
        
        prompt.append("""
            
            Please provide a JSON response with the following structure:
            {
                "severity": "low|medium|high",
                "conditions": [
                    {"name": "condition name", "confidence": 0.0-1.0}
                ],
                "advice": "general advice text",
                "seekMedicalAttention": true/false
            }
            
            Base severity on symptom urgency:
            - low: minor issues, wellness questions
            - medium: concerning symptoms that should be monitored
            - high: urgent symptoms requiring immediate medical attention
            """);
        
        return prompt.toString();
    }
}
