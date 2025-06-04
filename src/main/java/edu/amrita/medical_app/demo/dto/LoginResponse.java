package edu.amrita.medical_app.demo.dto;

import edu.amrita.medical_app.demo.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private String token;
    private String avatar;
    private String specialization;
    private String licenseNumber;
    private String affiliation;
    private Integer yearsOfExperience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}