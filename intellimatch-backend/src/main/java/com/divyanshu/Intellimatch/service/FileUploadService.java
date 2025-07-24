package com.divyanshu.Intellimatch.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;
    private final FileDeleteService fileDeleteService;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadToS3(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }
        
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File must have a valid name");
        }
        
        String sanitizedFileName = originalFileName
                .replaceAll("[\\s+]+", "-") // Replace spaces and plus signs with -
                .replaceAll("[^a-zA-Z0-9._-]", ""); // Remove any unsafe characters except . _ -
        String key = folder + "/" + UUID.randomUUID() + "-" + sanitizedFileName;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    public String uploadResume(MultipartFile resume) throws IOException {
        return uploadToS3(resume, "resumes");
    }

    public String uploadJobDescription(MultipartFile jobDescription) throws IOException {
        return uploadToS3(jobDescription, "job-descriptions");
    }
    
    /**
     * Delete files from S3 in case of rollback/cleanup scenarios
     */
    public void cleanupFiles(String resumeUrl, String jobDescriptionUrl) {
        try {
            if (resumeUrl != null && !resumeUrl.isEmpty()) {
                fileDeleteService.deleteResume(resumeUrl);
            }
        } catch (Exception e) {
            // Log but don't fail the cleanup process
            System.err.println("Failed to delete resume from S3: " + e.getMessage());
        }
        
        try {
            if (jobDescriptionUrl != null && !jobDescriptionUrl.isEmpty()) {
                fileDeleteService.deleteJobDescription(jobDescriptionUrl);
            }
        } catch (Exception e) {
            // Log but don't fail the cleanup process
            System.err.println("Failed to delete job description from S3: " + e.getMessage());
        }
    }
}
