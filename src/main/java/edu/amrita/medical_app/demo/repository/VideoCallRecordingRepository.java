package edu.amrita.medical_app.demo.repository;

import edu.amrita.medical_app.demo.entity.Appointment;
import edu.amrita.medical_app.demo.entity.VideoCallRecording;
import edu.amrita.medical_app.demo.entity.RecordingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoCallRecordingRepository extends JpaRepository<VideoCallRecording, Long> {
    
    Optional<VideoCallRecording> findByRecordingId(String recordingId);
    
    List<VideoCallRecording> findByAppointment(Appointment appointment);
    
    List<VideoCallRecording> findByAppointmentAndStatus(Appointment appointment, RecordingStatus status);
    
    Optional<VideoCallRecording> findByAppointmentAndRecordingId(Appointment appointment, String recordingId);
}
