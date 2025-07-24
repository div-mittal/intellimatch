package com.divyanshu.Intellimatch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "resume_matches")
public class ResumeMatch {
    @Id
    String id;
    String userId;
    String resumeName;
    String jobDescriptionName;
    String resumeUrl;
    String jobDescriptionUrl;
    Date matchDate;
    String matchResultId;

    public ResumeMatch(String id, String userId, String resumeName, String jobDescriptionName, String resumeUrl, String jobDescriptionUrl, String matchResultId) {
        this.id = id;
        this.userId = userId;
        this.resumeName = resumeName;
        this.jobDescriptionName = jobDescriptionName;
        this.resumeUrl = resumeUrl;
        this.jobDescriptionUrl = jobDescriptionUrl;
        this.matchDate = new Date();
        this.matchResultId = matchResultId;
    }
    
    public ResumeMatch() {
        // Default constructor
    }

    @Override
    public String toString() {
        return "ResumeMatch{" +
                "id='" + id + '\'' +
                ", resumeName='" + resumeName + '\'' +
                ", jobDescriptionName='" + jobDescriptionName + '\'' +
                ", resumeUrl='" + resumeUrl + '\'' +
                ", jobDescriptionUrl='" + jobDescriptionUrl + '\'' +
                ", matchDate=" + matchDate +
                ", matchResultId='" + matchResultId + '\'' +
                '}';
    }
}
