package edu.amrita.medical_app.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StopRecordingRequest {
    @NotBlank(message = "Recording ID is required")
    private String recordingId;
}
