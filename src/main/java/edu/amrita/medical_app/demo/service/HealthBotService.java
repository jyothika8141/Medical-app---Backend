package edu.amrita.medical_app.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.amrita.medical_app.demo.dto.*;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.entity.UserRole;
import edu.amrita.medical_app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HealthBotService {

    @Autowired
    private GeminiAIService geminiAIService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public HealthBotMessage sendMessage(HealthBotRequest request) {
        try {
            // Get conversation history as strings
            List<String> conversationHistory = new ArrayList<>();
            if (request.getHistory() != null) {
                conversationHistory = request.getHistory().stream()
                    .map(msg -> msg.getRole() + ": " + msg.getContent())
                    .collect(Collectors.toList());
            }

            // Get AI response from Gemini
            String aiResponse = geminiAIService.generateHealthResponse(request.getMessage(), conversationHistory);

            // Create response message
            HealthBotMessage response = new HealthBotMessage();
            response.setId(UUID.randomUUID().toString());
            response.setRole("assistant");
            response.setContent(aiResponse);
            response.setTimestamp(LocalDateTime.now());

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            // Return error message
            HealthBotMessage errorResponse = new HealthBotMessage();
            errorResponse.setId(UUID.randomUUID().toString());
            errorResponse.setRole("assistant");
            errorResponse.setContent("I'm sorry, I'm experiencing technical difficulties. Please try again later or consult with a healthcare professional.");
            errorResponse.setTimestamp(LocalDateTime.now());
            return errorResponse;
        }
    }

    public HealthAnalysisResponse analyzeConversation(HealthAnalysisRequest request) {
        try {
            // Convert conversation to string format
            List<String> conversationHistory = request.getConversation().stream()
                .map(msg -> msg.getRole() + ": " + msg.getContent())
                .collect(Collectors.toList());

            // Get analysis from Gemini AI
            String analysisJson = geminiAIService.analyzeHealthSymptoms(conversationHistory);

            // Try to parse JSON response
            try {
                JsonNode jsonNode = objectMapper.readTree(analysisJson);
                
                HealthAnalysisResponse response = new HealthAnalysisResponse();
                response.setSeverity(jsonNode.get("severity").asText("medium"));
                response.setAdvice(jsonNode.get("advice").asText("Please consult with a healthcare professional for personalized advice."));
                response.setSeekMedicalAttention(jsonNode.get("seekMedicalAttention").asBoolean(false));

                // Parse conditions
                List<HealthCondition> conditions = new ArrayList<>();
                JsonNode conditionsNode = jsonNode.get("conditions");
                if (conditionsNode != null && conditionsNode.isArray()) {
                    for (JsonNode conditionNode : conditionsNode) {
                        HealthCondition condition = new HealthCondition();
                        condition.setName(conditionNode.get("name").asText());
                        condition.setConfidence(conditionNode.get("confidence").asDouble());
                        conditions.add(condition);
                    }
                }
                response.setConditions(conditions);

                return response;

            } catch (Exception jsonException) {
                // If JSON parsing fails, create a basic response
                return createBasicAnalysisResponse(analysisJson);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorAnalysisResponse();
        }
    }

    public boolean shareConversationWithDoctor(Long doctorId, ShareConversationRequest request) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

            User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

            if (doctor.getRole() != UserRole.DOCTOR) {
                throw new IllegalArgumentException("User is not a doctor");
            }

            // Create or get chat with doctor
            ChatResponse chat = chatService.getOrCreateChatWithUser(doctorId);

            // Format conversation for sharing
            StringBuilder conversationText = new StringBuilder();
            conversationText.append("🤖 Health Bot Conversation Shared by ").append(currentUser.getFullName()).append("\n");
            conversationText.append("📅 Shared on: ").append(LocalDateTime.now()).append("\n\n");
            conversationText.append("--- Conversation History ---\n");

            for (HealthBotMessage message : request.getConversation()) {
                String role = message.getRole().equals("user") ? "Patient" : "HealthBot";
                conversationText.append("[").append(role).append("]: ").append(message.getContent()).append("\n");
            }

            conversationText.append("\n--- End of Conversation ---\n");
            conversationText.append("Please review this conversation and provide your professional medical advice.");

            // Send as message to doctor
            SendMessageRequest messageRequest = new SendMessageRequest();
            messageRequest.setText(conversationText.toString());
            chatService.sendMessage(chat.getId(), messageRequest);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private HealthAnalysisResponse createBasicAnalysisResponse(String analysisText) {
        HealthAnalysisResponse response = new HealthAnalysisResponse();
        response.setSeverity("medium");
        response.setConditions(new ArrayList<>());
        response.setAdvice(analysisText);
        response.setSeekMedicalAttention(true);
        return response;
    }

    private HealthAnalysisResponse createErrorAnalysisResponse() {
        HealthAnalysisResponse response = new HealthAnalysisResponse();
        response.setSeverity("medium");
        response.setConditions(new ArrayList<>());
        response.setAdvice("Unable to analyze conversation at this time. Please consult with a healthcare professional for medical advice.");
        response.setSeekMedicalAttention(true);
        return response;
    }
}
