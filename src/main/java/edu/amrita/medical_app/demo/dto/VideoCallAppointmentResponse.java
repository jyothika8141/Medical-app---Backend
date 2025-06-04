package edu.amrita.medical_app.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCallAppointmentResponse {
    private Long id;
    private DoctorInfo doctor;
    private PatientInfo patient;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String type;
    private String reasonForVisit;
    private String notes;
    private String meetingLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
