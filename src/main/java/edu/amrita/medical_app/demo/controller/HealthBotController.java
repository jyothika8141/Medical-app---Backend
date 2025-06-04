package edu.amrita.medical_app.demo.controller;

import edu.amrita.medical_app.demo.dto.*;
import edu.amrita.medical_app.demo.service.HealthBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/health-bot")
public class HealthBotController {

    @Autowired
    private HealthBotService healthBotService;

    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@RequestBody HealthBotRequest request) {
        try {
            HealthBotMessage response = healthBotService.sendMessage(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while processing your message");
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeConversation(@RequestBody HealthAnalysisRequest request) {
        try {
            HealthAnalysisResponse response = healthBotService.analyzeConversation(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while analyzing the conversation");
        }
    }

    @PostMapping("/share/{doctorId}")
    public ResponseEntity<?> shareConversationWithDoctor(
            @PathVariable Long doctorId, 
            @RequestBody ShareConversationRequest request) {
        try {
            boolean success = healthBotService.shareConversationWithDoctor(doctorId, request);
            return ResponseEntity.ok(Map.of("success", success));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while sharing the conversation");
        }
    }
}
