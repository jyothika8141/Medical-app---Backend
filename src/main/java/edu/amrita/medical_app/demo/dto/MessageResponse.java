package edu.amrita.medical_app.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String text;
    private boolean isRead;
    private LocalDateTime createdAt;
}
