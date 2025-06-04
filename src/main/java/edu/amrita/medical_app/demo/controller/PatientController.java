package edu.amrita.medical_app.demo.controller;

import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        try {
            User patient = patientService.getPatientById(id);
            return ResponseEntity.ok(patient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching the patient");
        }
    }

    @GetMapping("/me/doctors")
    public ResponseEntity<?> getMyDoctors() {
        try {
            List<User> doctors = patientService.getMyDoctors();
            return ResponseEntity.ok(doctors);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching doctors");
        }
    }

    @GetMapping("/me/health-statistics")
    public ResponseEntity<?> getHealthStatistics() {
        try {
            // TODO: Implement health statistics logic
            return ResponseEntity.ok("Health statistics feature coming soon");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching health statistics");
        }
    }
}
