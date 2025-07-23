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

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadToS3(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }
        String originalFileName = file.getOriginalFilename()
                .replaceAll("[\\s+]+", "-") // Replace spaces and plus signs with -
                .replaceAll("[^a-zA-Z0-9._-]", ""); // Remove any unsafe characters except . _ -
        String key = folder + "/" + UUID.randomUUID() + "-" + originalFileName;

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
}
