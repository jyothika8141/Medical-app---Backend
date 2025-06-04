package edu.amrita.medical_app.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank(message = "Message text is required")
    private String text;
}
