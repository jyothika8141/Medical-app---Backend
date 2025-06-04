package edu.amrita.medical_app.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private Long id;
    private List<ParticipantResponse> participants;
    private MessageResponse lastMessage;
    private int unreadCount;
}
