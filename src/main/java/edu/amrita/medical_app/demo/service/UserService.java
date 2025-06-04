package edu.amrita.medical_app.demo.service;

import edu.amrita.medical_app.demo.dto.RegistrationRequest;
import edu.amrita.medical_app.demo.dto.UpdatePasswordRequest;
import edu.amrita.medical_app.demo.dto.UpdateUserProfileRequest;
import edu.amrita.medical_app.demo.entity.DoctorDetails;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.entity.UserRole;
import edu.amrita.medical_app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegistrationRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        // If doctor, validate and set doctor details
        if (request.getRole() == UserRole.DOCTOR) {
            validateDoctorDetails(request);
            DoctorDetails doctorDetails = new DoctorDetails();
            doctorDetails.setLicenseNumber(request.getLicenseNumber());
            doctorDetails.setSpecialization(request.getSpecialization());
            doctorDetails.setAffiliation(request.getAffiliation());
            doctorDetails.setYearsOfExperience(request.getYearsOfExperience());
            doctorDetails.setUser(user);
            user.setDoctorDetails(doctorDetails);
        }

        return userRepository.save(user);
    }

    private void validateDoctorDetails(RegistrationRequest request) {
        if (request.getLicenseNumber() == null || request.getLicenseNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("License number is required for doctors");
        }
        if (request.getSpecialization() == null || request.getSpecialization().trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization is required for doctors");
        }
        if (request.getAffiliation() == null || request.getAffiliation().trim().isEmpty()) {
            throw new IllegalArgumentException("Affiliation is required for doctors");
        }
        if (request.getYearsOfExperience() == null || request.getYearsOfExperience() < 0) {
            throw new IllegalArgumentException("Years of experience is required for doctors");
        }
    }

    public User getCurrentUserProfile() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public User updateUserProfile(UpdateUserProfileRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update basic user fields
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Check if new email already exists
            if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        // Update doctor-specific fields if user is a doctor
        if (user.getRole() == UserRole.DOCTOR && user.getDoctorDetails() != null) {
            DoctorDetails doctorDetails = user.getDoctorDetails();
            if (request.getSpecialization() != null && !request.getSpecialization().trim().isEmpty()) {
                doctorDetails.setSpecialization(request.getSpecialization());
            }
            if (request.getLicenseNumber() != null && !request.getLicenseNumber().trim().isEmpty()) {
                doctorDetails.setLicenseNumber(request.getLicenseNumber());
            }
            if (request.getAffiliation() != null && !request.getAffiliation().trim().isEmpty()) {
                doctorDetails.setAffiliation(request.getAffiliation());
            }
            if (request.getYearsOfExperience() != null && request.getYearsOfExperience() >= 0) {
                doctorDetails.setYearsOfExperience(request.getYearsOfExperience());
            }
        }

        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(UpdatePasswordRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}