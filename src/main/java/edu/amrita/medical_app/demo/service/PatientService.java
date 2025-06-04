package edu.amrita.medical_app.demo.service;

import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.entity.UserRole;
import edu.amrita.medical_app.demo.repository.AppointmentRepository;
import edu.amrita.medical_app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public User getPatientById(Long id) {
        User patient = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        
        if (patient.getRole() != UserRole.PATIENT) {
            throw new IllegalArgumentException("User is not a patient");
        }
        
        return patient;
    }

    public List<User> getMyDoctors() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (currentUser.getRole() != UserRole.PATIENT) {
            throw new IllegalArgumentException("Only patients can access doctor list");
        }

        // Get all doctors who have appointments with this patient
        return appointmentRepository.findByPatient(currentUser)
            .stream()
            .map(appointment -> appointment.getDoctor())
            .distinct()
            .collect(Collectors.toList());
    }
}
