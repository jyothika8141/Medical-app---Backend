package edu.amrita.medical_app.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthBotMessage {
    private String id;
    private String role; // "user" or "assistant"
    private String content;
    private LocalDateTime timestamp;
}
