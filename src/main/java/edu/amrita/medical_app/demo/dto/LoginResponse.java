package edu.amrita.medical_app.demo.dto;

import edu.amrita.medical_app.demo.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
    private UserRole role;
    private String fullName;
} 