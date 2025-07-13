package com.divyanshu.Intellimatch.repository;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "user_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {
    @Id
    private String id;
    private String resumeUrl;
    private String jobDescriptionUrl;
    private LocalDateTime uploadedAt;
    private String userId; // or email or username
}



