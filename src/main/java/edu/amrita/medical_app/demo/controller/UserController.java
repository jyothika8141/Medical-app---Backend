package edu.amrita.medical_app.demo.controller;

import edu.amrita.medical_app.demo.dto.RegistrationRequest;
import edu.amrita.medical_app.demo.dto.UpdatePasswordRequest;
import edu.amrita.medical_app.demo.dto.UpdateUserProfileRequest;
import edu.amrita.medical_app.demo.entity.User;
import edu.amrita.medical_app.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
        try {
            User user = userService.registerUser(request);
            return ResponseEntity.ok().body("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred during registration");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            User user = userService.getCurrentUserProfile();
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while fetching user profile");
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateUserProfileRequest request) {
        try {
            User user = userService.updateUserProfile(request);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while updating user profile");
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        try {
            userService.updatePassword(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while updating password");
        }
    }
}