package edu.amrita.medical_app.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {
    private Long id;
    private String fullName;
    private String avatar;
    private String role;
    private boolean isOnline;
    private String lastSeen;
}
