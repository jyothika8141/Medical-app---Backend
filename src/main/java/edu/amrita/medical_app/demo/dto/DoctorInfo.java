package edu.amrita.medical_app.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorInfo {
    private Long id;
    private String fullName;
    private String email;
    private String specialization;
    private String avatar;
    private String affiliation;
    private Integer yearsOfExperience;
}
