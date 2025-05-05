package edu.amrita.medical_app.demo.service;

import edu.amrita.medical_app.demo.dto.LoginRequest;
import edu.amrita.medical_app.demo.dto.LoginResponse;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = null;
        if (request.isRememberMe()) {
            token = generateRememberMeToken();
            // In a real application, you would store this token in the database
            // with an expiration date and associate it with the user
        }

        return new LoginResponse(
            "Login successful",
            token,
            user.getRole(),
            user.getFullName()
        );
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

    private String generateRememberMeToken() {
        return UUID.randomUUID().toString();
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
} 