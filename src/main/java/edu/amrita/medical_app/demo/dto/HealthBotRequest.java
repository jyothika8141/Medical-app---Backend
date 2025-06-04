package edu.amrita.medical_app.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class HealthBotRequest {
    @NotBlank(message = "Message is required")
    private String message;
    
    private List<HealthBotMessage> history;
}
