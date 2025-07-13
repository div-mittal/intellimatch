package com.divyanshu.Intellimatch.controller;

import com.divyanshu.Intellimatch.model.UserDocumentRepository;
import com.divyanshu.Intellimatch.repository.UserDocument;
import com.divyanshu.Intellimatch.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadService fileUploadService;
    private final UserDocumentRepository userRepo;

    @PostMapping
    public ResponseEntity<?> uploadFiles(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobDescription") MultipartFile jobDescription,
            @RequestParam("userId") String userId // optional for now
    ) {
        try {
            String resumeUrl = fileUploadService.uploadToS3(resume, "resumes");
            String jdUrl = fileUploadService.uploadToS3(jobDescription, "job-descriptions");

            UserDocument document = new UserDocument(
                    null,
                    resumeUrl,
                    jdUrl,
                    LocalDateTime.now(),
                    userId
            );

            userRepo.save(document);
            return ResponseEntity.ok(document);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed.");
        }
    }
}
