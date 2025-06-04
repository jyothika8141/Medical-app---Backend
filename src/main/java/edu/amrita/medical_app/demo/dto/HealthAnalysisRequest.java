package edu.amrita.medical_app.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class HealthAnalysisRequest {
    private List<HealthBotMessage> conversation;
}
