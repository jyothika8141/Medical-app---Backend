package edu.amrita.medical_app.demo.service;

import edu.amrita.medical_app.demo.dto.CreateAppointmentRequest;
import edu.amrita.medical_app.demo.entity.Appointment;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.entity.UserRole;
import edu.amrita.medical_app.demo.repository.AppointmentRepository;
import edu.amrita.medical_app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Appointment createAppointment(CreateAppointmentRequest request) {
        // Get the currently logged-in user
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate that the current user is a patient
        if (currentUser.getRole() != UserRole.PATIENT) {
            throw new IllegalArgumentException("Only patients can create appointments");
        }

        // Validate that the current user is the patient in the request
        if (!currentUser.getId().equals(request.getPatientId())) {
            throw new IllegalArgumentException("You can only create appointments for yourself");
        }

        // Validate doctor exists and is a doctor
        User doctor = userRepository.findById(request.getDoctorId())
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new IllegalArgumentException("User is not a doctor");
        }

        // Validate time slot is available
        if (isTimeSlotBooked(doctor, request.getDate(), request.getStartTime(), request.getEndTime())) {
            throw new IllegalArgumentException("Time slot is already booked");
        }

        // Create and save appointment
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(currentUser);
        appointment.setDate(request.getDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setType(request.getType());
        appointment.setReasonForVisit(request.getReasonForVisit());
        appointment.setNotes(request.getNotes());

        return appointmentRepository.save(appointment);
    }

    private boolean isTimeSlotBooked(User doctor, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return appointmentRepository.existsByDoctorAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            doctor, date, endTime, startTime);
    }
} 