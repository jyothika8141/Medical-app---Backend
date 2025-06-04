package edu.amrita.medical_app.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthCondition {
    private String name;
    private double confidence;
}
