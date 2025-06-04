package edu.amrita.medical_app.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthAnalysisResponse {
    private String severity; // "low", "medium", "high"
    private List<HealthCondition> conditions;
    private String advice;
    private boolean seekMedicalAttention;
}
