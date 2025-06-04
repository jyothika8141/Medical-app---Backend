package edu.amrita.medical_app.demo.dto;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    private String fullName;
    private String email;
    private String avatar;
    private String specialization;
    private String licenseNumber;
    private String affiliation;
    private Integer yearsOfExperience;
}
