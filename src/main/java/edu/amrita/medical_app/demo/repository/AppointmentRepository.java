package edu.amrita.medical_app.demo.repository;

import edu.amrita.medical_app.demo.entity.Appointment;
import edu.amrita.medical_app.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndDate(User doctor, LocalDate date);
    List<Appointment> findByPatientAndDate(User patient, LocalDate date);
    boolean existsByDoctorAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
        User doctor, LocalDate date, LocalTime endTime, LocalTime startTime);
} 