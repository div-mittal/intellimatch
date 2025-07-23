package com.divyanshu.Intellimatch.dto;

import com.divyanshu.Intellimatch.model.MatchResult;
import com.divyanshu.Intellimatch.model.ResumeMatch;
import lombok.Data;

import java.util.Date;

@Data
public class MatchHistoryDTO {
    private String id;
    private String resumeName;
    private String jobDescriptionName;
    private String resumeUrl;
    private String jobDescriptionUrl;
    private Date matchDate;
    private int score;
    private String resultMessage;
    private MatchResult matchResult; // Full match result for detailed view

    public MatchHistoryDTO(ResumeMatch resumeMatch, MatchResult matchResult) {
        this.id = resumeMatch.getId();
        this.resumeName = resumeMatch.getResumeName();
        this.jobDescriptionName = resumeMatch.getJobDescriptionName();
        this.resumeUrl = resumeMatch.getResumeUrl();
        this.jobDescriptionUrl = resumeMatch.getJobDescriptionUrl();
        this.matchDate = resumeMatch.getMatchDate();
        
        if (matchResult != null) {
            this.score = matchResult.getAtsScorePercent();
            this.resultMessage = matchResult.getSummary();
            this.matchResult = matchResult;
        } else {
            this.score = 0;
            this.resultMessage = "Analysis in progress...";
            this.matchResult = null;
        }
    }
}
