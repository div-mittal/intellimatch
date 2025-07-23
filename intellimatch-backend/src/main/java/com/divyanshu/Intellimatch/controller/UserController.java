package com.divyanshu.Intellimatch.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.divyanshu.Intellimatch.dto.LoginRequest;
import com.divyanshu.Intellimatch.dto.MatchHistoryDTO;
import com.divyanshu.Intellimatch.model.MatchResult;
import com.divyanshu.Intellimatch.model.ResumeMatch;
import com.divyanshu.Intellimatch.model.User;
import com.divyanshu.Intellimatch.repository.MatchResultRepository;
import com.divyanshu.Intellimatch.service.UserService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final MatchResultRepository matchResultRepository;

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
                    .maxAge(24 * 60 * 60)
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
                    .maxAge(24 * 60 * 60)
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

    // Get user by ID route
    @GetMapping("/get")
    public ResponseEntity<?> getUserById(@CookieValue(value = "userId", required = false) String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(401).body("Authentication required.");
        }

        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Logout route
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // Clear the userId cookie
        ResponseCookie cookie = ResponseCookie.from("userId", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
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
            
            // Convert ResumeMatch list to MatchHistoryDTO list
            List<MatchHistoryDTO> historyDTOs = new ArrayList<>();
            for (ResumeMatch resumeMatch : user.getHistory()) {
                MatchResult matchResult = null;
                if (resumeMatch.getMatchResultId() != null) {
                    matchResult = matchResultRepository.findById(resumeMatch.getMatchResultId()).orElse(null);
                }
                historyDTOs.add(new MatchHistoryDTO(resumeMatch, matchResult));
            }
            
            return ResponseEntity.ok(historyDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        } 
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<?> getMatchDetails(
        @PathVariable String matchId,
        @CookieValue(value = "userId", required = false) String userId
    ) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(401).body("Authentication required.");
        }

        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Find the specific match in user's history
            ResumeMatch resumeMatch = user.getHistory().stream()
                .filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Match not found"));
            
            MatchResult matchResult = null;
            if (resumeMatch.getMatchResultId() != null) {
                matchResult = matchResultRepository.findById(resumeMatch.getMatchResultId()).orElse(null);
            }
            
            return ResponseEntity.ok(new MatchHistoryDTO(resumeMatch, matchResult));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
