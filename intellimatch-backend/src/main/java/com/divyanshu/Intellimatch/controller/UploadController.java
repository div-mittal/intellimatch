package com.divyanshu.Intellimatch.controller;

import com.divyanshu.Intellimatch.service.ResumeMatchService;
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
    private final ResumeMatchService resumeMatchService;
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

            String resumeName = resume.getOriginalFilename();
            String jobDescriptionName = jobDescription.getOriginalFilename();

            String resumeUrl = null;
            String jdUrl = null;
            ResumeMatch savedMatch = null;

            try {
                // Upload files to S3
                resumeUrl = fileUploadService.uploadResume(resume);
                jdUrl = fileUploadService.uploadJobDescription(jobDescription);

                // Create and save ResumeMatch
                ResumeMatch document = new ResumeMatch(
                    null,
                    user.getId(),
                    resumeName,
                    jobDescriptionName,
                    resumeUrl,
                    jdUrl,
                    null // matchResultId will be set later after processing the match
                );

                savedMatch = resumeMatchService.save(document);
                
                if (savedMatch == null) {
                    throw new RuntimeException("Failed to save match document");
                }
                
                // Add to user's history
                user.addToHistory(savedMatch);
                User updatedUser = userService.save(user);
                
                if (updatedUser == null) {
                    throw new RuntimeException("Failed to update user history");
                }
                
                return ResponseEntity.ok(savedMatch);
                
            } catch (Exception uploadException) {
                // Cleanup uploaded files if any step fails
                if (resumeUrl != null || jdUrl != null) {
                    fileUploadService.cleanupFiles(resumeUrl, jdUrl);
                }
                
                // If match was saved but user history update failed, we let the async cleanup handle it
                // since the async process will fail and trigger full cleanup
                
                throw uploadException; // Re-throw to be caught by outer catch block
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed.");
        }
    }
}
