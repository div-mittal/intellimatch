package com.divyanshu.Intellimatch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

@Data
@Document(collection = "users")
public class User {
    @Id
    String id;
    
    @NotBlank(message = "Name is required")
    String name;
    
    String phoneNumber;  // optional, can be null
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    String email;
    
    ArrayList<ResumeMatch> history;

    public User(String id, String name, String phoneNumber, String password, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber; // can be null
        this.password = hashPassword(password);
        this.email = email;
        this.history = new ArrayList<>();
    }

    public User(String id, String name, String password, String email) {
        this(id, name, null, password, email); // phoneNumber omitted
    }

    public User() {
        this.history = new ArrayList<>();
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public void addToHistory(ResumeMatch resumeMatch) {
        history.add(resumeMatch);
    }

}
