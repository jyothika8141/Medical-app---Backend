package edu.amrita.medical_app.demo.service;

import edu.amrita.medical_app.demo.dto.*;
import edu.amrita.medical_app.demo.entity.*;
import edu.amrita.medical_app.demo.repository.AppointmentRepository;
import edu.amrita.medical_app.demo.repository.UserRepository;
import edu.amrita.medical_app.demo.repository.VideoCallRecordingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VideoCallService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private VideoCallRecordingRepository recordingRepository;

    @Autowired
    private UserRepository userRepository;

    public VideoCallAppointmentResponse getAppointmentForVideoCall(Long appointmentId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Check if user has access to this appointment
        if (!appointment.getPatient().getId().equals(currentUser.getId()) && 
            !appointment.getDoctor().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Access denied to this appointment");
        }

        // Check if appointment is for video call
        if (!"VIDEO_CALL".equals(appointment.getType())) {
            throw new IllegalArgumentException("This appointment is not scheduled for a video call");
        }

        // Generate meeting link if not exists
        if (appointment.getMeetingLink() == null || appointment.getMeetingLink().isEmpty()) {
            String meetingLink = generateMeetingLink(appointmentId);
            appointment.setMeetingLink(meetingLink);
            appointmentRepository.save(appointment);
        }

        // Convert to response DTO
        VideoCallAppointmentResponse response = new VideoCallAppointmentResponse();
        response.setId(appointment.getId());
        response.setDate(appointment.getDate());
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());
        response.setStatus(appointment.getStatus().toString());
        response.setType(appointment.getType());
        response.setReasonForVisit(appointment.getReasonForVisit());
        response.setNotes(appointment.getNotes());
        response.setMeetingLink(appointment.getMeetingLink());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());

        // Set doctor info
        User doctor = appointment.getDoctor();
        DoctorInfo doctorInfo = new DoctorInfo();
        doctorInfo.setId(doctor.getId());
        doctorInfo.setFullName(doctor.getFullName());
        doctorInfo.setEmail(doctor.getEmail());
        doctorInfo.setAvatar(doctor.getAvatar());
        if (doctor.getDoctorDetails() != null) {
            doctorInfo.setSpecialization(doctor.getDoctorDetails().getSpecialization());
            doctorInfo.setAffiliation(doctor.getDoctorDetails().getAffiliation());
            doctorInfo.setYearsOfExperience(doctor.getDoctorDetails().getYearsOfExperience());
        }
        response.setDoctor(doctorInfo);

        // Set patient info
        User patient = appointment.getPatient();
        PatientInfo patientInfo = new PatientInfo();
        patientInfo.setId(patient.getId());
        patientInfo.setFullName(patient.getFullName());
        patientInfo.setEmail(patient.getEmail());
        patientInfo.setAvatar(patient.getAvatar());
        response.setPatient(patientInfo);

        return response;
    }

    @Transactional
    public StartRecordingResponse startRecording(Long appointmentId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Check if user has access to this appointment
        if (!appointment.getPatient().getId().equals(currentUser.getId()) && 
            !appointment.getDoctor().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Access denied to this appointment");
        }

        // Check if appointment is for video call
        if (!"VIDEO_CALL".equals(appointment.getType())) {
            throw new IllegalArgumentException("This appointment is not scheduled for a video call");
        }

        // Generate unique recording ID
        String recordingId = "REC_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        // Create recording record
        VideoCallRecording recording = new VideoCallRecording();
        recording.setRecordingId(recordingId);
        recording.setAppointment(appointment);
        recording.setStartTime(LocalDateTime.now());
        recording.setStatus(RecordingStatus.RECORDING);

        recordingRepository.save(recording);

        return new StartRecordingResponse(recordingId);
    }

    @Transactional
    public StopRecordingResponse stopRecording(Long appointmentId, StopRecordingRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Check if user has access to this appointment
        if (!appointment.getPatient().getId().equals(currentUser.getId()) && 
            !appointment.getDoctor().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Access denied to this appointment");
        }

        VideoCallRecording recording = recordingRepository.findByAppointmentAndRecordingId(appointment, request.getRecordingId())
            .orElseThrow(() -> new IllegalArgumentException("Recording not found"));

        if (recording.getStatus() != RecordingStatus.RECORDING) {
            throw new IllegalArgumentException("Recording is not currently active");
        }

        // Stop recording
        recording.setEndTime(LocalDateTime.now());
        recording.setStatus(RecordingStatus.COMPLETED);
        
        // Generate recording URL (in a real implementation, this would be from a video service)
        String recordingUrl = generateRecordingUrl(recording.getRecordingId());
        recording.setRecordingUrl(recordingUrl);

        recordingRepository.save(recording);

        return new StopRecordingResponse(recordingUrl);
    }

    private String generateMeetingLink(Long appointmentId) {
        // In a real implementation, this would integrate with a video service like Zoom, Teams, etc.
        String meetingId = "MEET_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return "https://healthconnect.video/meeting/" + meetingId + "?appointment=" + appointmentId;
    }

    private String generateRecordingUrl(String recordingId) {
        // In a real implementation, this would be the actual URL from the video service
        return "https://healthconnect.video/recordings/" + recordingId + ".mp4";
    }
}
