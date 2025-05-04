package edu.amrita.medical_app.demo.service;

import edu.amrita.medical_app.demo.dto.RegistrationRequest;
import edu.amrita.medical_app.demo.entity.DoctorDetails;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.entity.UserRole;
import edu.amrita.medical_app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
} 