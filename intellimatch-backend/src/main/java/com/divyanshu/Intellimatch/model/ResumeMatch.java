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
    String jobRole;
    String resumeName;
    String jobDescriptionName;
    String resumeUrl;
    String jobDescriptionUrl;
    Date matchDate;
    double score;
    String resultMessage;


    public ResumeMatch(String id, String jobRole, String resumeName, String jobDescriptionName, String resumeUrl, String jobDescriptionUrl, double score, String resultMessage) {
        this.id = id;
        this.jobRole = jobRole;
        this.resumeName = resumeName;
        this.jobDescriptionName = jobDescriptionName;
        this.resumeUrl = resumeUrl;
        this.jobDescriptionUrl = jobDescriptionUrl;
        this.matchDate = new Date();
        this.score = score;
        this.resultMessage = resultMessage;
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
                ", score=" + score +
                ", resultMessage='" + resultMessage + '\'' +
                '}';
    }
}
