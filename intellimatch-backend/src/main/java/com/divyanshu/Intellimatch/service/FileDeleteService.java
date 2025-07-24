package com.divyanshu.Intellimatch.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileDeleteService {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            log.warn("Attempted to delete file with null or empty URL");
            throw new IllegalArgumentException("File URL must not be empty");
        }

        try {
            String key = fileUrl.replace("https://" + bucketName + ".s3.amazonaws.com/", "");
            
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
            log.info("Successfully deleted file from S3: {}", key);
            
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public void deleteResume(String resumeUrl) {
        log.info("Deleting resume file: {}", resumeUrl);
        deleteFile(resumeUrl);
    }

    public void deleteJobDescription(String jobDescriptionUrl) {
        log.info("Deleting job description file: {}", jobDescriptionUrl);
        deleteFile(jobDescriptionUrl);
    }    
}
