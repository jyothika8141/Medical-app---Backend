package edu.amrita.medical_app.demo.service;

import edu.amrita.medical_app.demo.dto.LoginRequest;
import edu.amrita.medical_app.demo.dto.LoginResponse;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.entity.UserRole;
import edu.amrita.medical_app.demo.repository.UserRepository;
import edu.amrita.medical_app.demo.config.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String jwtToken = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setToken(jwtToken);
        response.setAvatar(user.getAvatar());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setMessage("Login successful");

        // Add doctor-specific fields if user is a doctor
        if (user.getRole() == UserRole.DOCTOR && user.getDoctorDetails() != null) {
            response.setSpecialization(user.getDoctorDetails().getSpecialization());
            response.setLicenseNumber(user.getDoctorDetails().getLicenseNumber());
            response.setAffiliation(user.getDoctorDetails().getAffiliation());
            response.setYearsOfExperience(user.getDoctorDetails().getYearsOfExperience());
        }

        return response;
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        String resetToken = generateResetToken();
        // In a real application, you would:
        // 1. Store the reset token in the database with an expiration date
        // 2. Send an email to the user with a link containing the token
        // 3. Implement a separate endpoint to handle the actual password reset
    }

    private String generateResetToken() {
        return java.util.UUID.randomUUID().toString();
    }
} 