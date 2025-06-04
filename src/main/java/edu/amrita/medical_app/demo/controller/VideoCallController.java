package edu.amrita.medical_app.demo.controller;

import edu.amrita.medical_app.demo.dto.*;
import edu.amrita.medical_app.demo.service.VideoCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video-calls")
public class VideoCallController {

    @Autowired
    private VideoCallService videoCallService;

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getAppointmentForVideoCall(@PathVariable Long appointmentId) {
        try {
            VideoCallAppointmentResponse response = videoCallService.getAppointmentForVideoCall(appointmentId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching appointment details");
        }
    }

    @PostMapping("/appointment/{appointmentId}/record/start")
    public ResponseEntity<?> startRecording(@PathVariable Long appointmentId) {
        try {
            StartRecordingResponse response = videoCallService.startRecording(appointmentId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while starting recording");
        }
    }

    @PostMapping("/appointment/{appointmentId}/record/stop")
    public ResponseEntity<?> stopRecording(@PathVariable Long appointmentId, @RequestBody StopRecordingRequest request) {
        try {
            StopRecordingResponse response = videoCallService.stopRecording(appointmentId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while stopping recording");
        }
    }
}
