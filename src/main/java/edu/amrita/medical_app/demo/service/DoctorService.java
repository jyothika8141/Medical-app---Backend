package edu.amrita.medical_app.demo.service;

import edu.amrita.medical_app.demo.dto.TimeSlotResponse;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.entity.UserRole;
import edu.amrita.medical_app.demo.repository.AppointmentRepository;
import edu.amrita.medical_app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<User> getAllDoctors(String specialization) {
        List<User> doctors = userRepository.findByRole(UserRole.DOCTOR);
        
        if (specialization != null && !specialization.trim().isEmpty()) {
            return doctors.stream()
                .filter(doctor -> doctor.getDoctorDetails() != null && 
                    doctor.getDoctorDetails().getSpecialization().toLowerCase()
                        .contains(specialization.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        return doctors;
    }

    public User getDoctorById(Long id) {
        User doctor = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new IllegalArgumentException("User is not a doctor");
        }
        
        return doctor;
    }

    public List<TimeSlotResponse> getAvailableTimeSlots(Long doctorId, LocalDate date) {
        User doctor = getDoctorById(doctorId);
        
        // Generate time slots for the day (9 AM to 5 PM, 1-hour slots)
        List<TimeSlotResponse> timeSlots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        
        while (startTime.isBefore(endTime)) {
            LocalTime slotEndTime = startTime.plusHours(1);
            
            // Check if this time slot is available
            boolean isAvailable = !appointmentRepository.existsByDoctorAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                doctor, date, slotEndTime, startTime);
            
            timeSlots.add(new TimeSlotResponse(date, startTime, slotEndTime, isAvailable));
            startTime = slotEndTime;
        }
        
        return timeSlots;
    }

    public List<User> getMyPatients() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (currentUser.getRole() != UserRole.DOCTOR) {
            throw new IllegalArgumentException("Only doctors can access patient list");
        }

        // Get all patients who have appointments with this doctor
        return appointmentRepository.findByDoctor(currentUser)
            .stream()
            .map(appointment -> appointment.getPatient())
            .distinct()
            .collect(Collectors.toList());
    }
}
