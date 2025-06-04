package edu.amrita.medical_app.demo.dto;

import edu.amrita.medical_app.demo.entity.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpdateAppointmentRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String type;
    private String reasonForVisit;
    private String notes;
}
