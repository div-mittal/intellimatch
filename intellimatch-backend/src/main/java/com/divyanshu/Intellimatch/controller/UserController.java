package com.divyanshu.Intellimatch.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.divyanshu.Intellimatch.dto.LoginRequest;
import com.divyanshu.Intellimatch.model.User;
import com.divyanshu.Intellimatch.service.UserService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // Login route
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            
            User user = userOpt.get();

            // Ensure user ID is not null before creating cookie
            if (user.getId() == null) {
                return ResponseEntity.status(500).body("User ID is missing.");
            }

            ResponseCookie cookie = ResponseCookie.from("userId", String.valueOf(user.getId()))
                    .httpOnly(true)
                    .path("/")
                    .maxAge(24 * 60 * 60) // 1 day
                    .build();

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Register route
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }
            User savedUser = userService.createUser(user);

            ResponseCookie cookie = ResponseCookie.from("userId", String.valueOf(user.getId()))
                    .httpOnly(true)
                    .path("/")
                    .maxAge(24 * 60 * 60) // 1 day
                    .build();

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }
            return ResponseEntity.ok(userOpt.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Logout route (dummy implementation)
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // Implement token/session invalidation logic if needed
        ResponseCookie cookie = ResponseCookie.from("userId", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) // Expire the cookie
                .build();
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<?> getUserHistory(
        @CookieValue(value = "userId", required = false) String userId
        ) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(401).body("Authentication required.");
        }

        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user.getHistory());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        } 
    }
}
