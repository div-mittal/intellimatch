package com.divyanshu.Intellimatch.controller;

import com.divyanshu.Intellimatch.repository.ResumeMatchRepository;
import com.divyanshu.Intellimatch.service.UserService;
import com.divyanshu.Intellimatch.model.ResumeMatch;
import com.divyanshu.Intellimatch.model.User;
import com.divyanshu.Intellimatch.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadService fileUploadService;
    private final ResumeMatchRepository resumeRepo;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> uploadFiles(
        @RequestParam("resume") MultipartFile resume,
        @RequestParam("jobDescription") MultipartFile jobDescription,
        @CookieValue(value = "userId", required = false) String userId
    ) {
        // Check if auth cookie is present
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required.");
        }

        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            if(resume == null || jobDescription == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Both files are required.");
            }

            // check file types
            String resumeContentType = resume.getContentType();
            String jdContentType = jobDescription.getContentType();
            
            // Allowed content types
            String[] allowedTypes = {
                "application/pdf", 
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            };
            
            boolean isResumeValid = resumeContentType != null && 
                (resumeContentType.equals(allowedTypes[0]) || resumeContentType.equals(allowedTypes[1]));
            boolean isJdValid = jdContentType != null && 
                (jdContentType.equals(allowedTypes[0]) || jdContentType.equals(allowedTypes[1]));
                
            if (!isResumeValid || !isJdValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only PDF and DOCX files are allowed.");
            }

            System.out.println("Uploading files for user: " + user.getName());
            System.out.println("Resume file: " + resume.getOriginalFilename());
            System.out.println("Job Description file: " + jobDescription.getOriginalFilename());

            String resumeName = resume.getOriginalFilename();
            String jobDescriptionName = jobDescription.getOriginalFilename();

            String resumeUrl = fileUploadService.uploadToS3(resume, "resumes");
            String jdUrl = fileUploadService.uploadToS3(jobDescription, "job-descriptions");

            ResumeMatch document = new ResumeMatch(
                null,
                resumeName,
                jobDescriptionName,
                resumeUrl,
                jdUrl,
                0.0,
                "Uploaded successfully"
            );

            ResumeMatch savedMatch = resumeRepo.save(document);
            
            // Add to user's history
            user.addToHistory(savedMatch);
            userService.save(user);
            
            return ResponseEntity.ok(savedMatch);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed.");
        }
    }
}
