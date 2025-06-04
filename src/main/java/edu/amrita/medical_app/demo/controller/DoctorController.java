package edu.amrita.medical_app.demo.controller;

import edu.amrita.medical_app.demo.dto.TimeSlotResponse;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<?> getAllDoctors(@RequestParam(required = false) String specialization) {
        try {
            List<User> doctors = doctorService.getAllDoctors(specialization);
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching doctors");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        try {
            User doctor = doctorService.getDoctorById(id);
            return ResponseEntity.ok(doctor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching the doctor");
        }
    }

    @GetMapping("/{doctorId}/time-slots")
    public ResponseEntity<?> getAvailableTimeSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<TimeSlotResponse> timeSlots = doctorService.getAvailableTimeSlots(doctorId, date);
            return ResponseEntity.ok(timeSlots);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching time slots");
        }
    }

    @GetMapping("/me/patients")
    public ResponseEntity<?> getMyPatients() {
        try {
            List<User> patients = doctorService.getMyPatients();
            return ResponseEntity.ok(patients);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching patients");
        }
    }
}
