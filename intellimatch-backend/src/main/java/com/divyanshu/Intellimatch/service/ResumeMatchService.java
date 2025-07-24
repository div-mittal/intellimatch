package com.divyanshu.Intellimatch.service;

import com.divyanshu.Intellimatch.model.MatchResult;
import com.divyanshu.Intellimatch.model.ResumeMatch;
import com.divyanshu.Intellimatch.model.User;
import com.divyanshu.Intellimatch.repository.MatchResultRepository;
import com.divyanshu.Intellimatch.repository.ResumeMatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeMatchService {

    private final FileDeleteService fileDeleteService;
    private final NlpAnalysisService nlpAnalysisService;
    private final MatchResultRepository matchResultRepository;
    private final ResumeMatchRepository resumeMatchRepository;
    private final UserService userService;

    public ResumeMatch save(ResumeMatch resumeMatch) {
        // Save the ResumeMatch first
        ResumeMatch savedMatch = resumeMatchRepository.save(resumeMatch);
        
        // Asynchronously process the match analysis
        processMatchAnalysisAsync(savedMatch);
        
        return savedMatch;
    }

    @Async
    public void processMatchAnalysisAsync(ResumeMatch resumeMatch) {
        try {
            log.info("Starting async analysis for ResumeMatch ID: {}", resumeMatch.getId());
            
            // Call the NLP API to get analysis results
            MatchResult matchResult = nlpAnalysisService.analyzeResumeMatch(
                resumeMatch.getResumeUrl(), 
                resumeMatch.getJobDescriptionUrl()
            );

            if (matchResult == null) {
                throw new RuntimeException("NLP analysis returned null result");
            }

            // Save the MatchResult
            MatchResult savedMatchResult = matchResultRepository.save(matchResult);
            
            // Update the ResumeMatch with the MatchResult ID
            resumeMatch.setMatchResultId(savedMatchResult.getId());
            resumeMatchRepository.save(resumeMatch);
            
            log.info("Analysis completed for ResumeMatch ID: {}, MatchResult ID: {}", 
                resumeMatch.getId(), savedMatchResult.getId());
                
        } catch (Exception e) {
            log.error("Error processing match analysis for ResumeMatch ID: {}. Starting cleanup...", 
                resumeMatch.getId(), e);
            
            // Create a failed analysis result instead of deleting everything
            // This gives users feedback that analysis failed rather than losing their upload
            createFailedAnalysisResult(resumeMatch, e.getMessage());
        }
    }
    
    private void createFailedAnalysisResult(ResumeMatch resumeMatch, String errorMessage) {
        try {
            // Create a MatchResult indicating analysis failure
            MatchResult failedResult = new MatchResult();
            failedResult.setAtsScorePercent(0);
            failedResult.setSummary("Analysis failed: " + errorMessage + ". Please try uploading again or contact support.");
            failedResult.setWhatMatched(new java.util.ArrayList<>());
            failedResult.setWhatIsMissing(new java.util.ArrayList<>());
            
            MatchResult savedResult = matchResultRepository.save(failedResult);
            resumeMatch.setMatchResultId(savedResult.getId());
            resumeMatchRepository.save(resumeMatch);
            
            log.info("Created failed analysis result for ResumeMatch ID: {}", resumeMatch.getId());
            
        } catch (Exception saveException) {
            log.error("Failed to create failed analysis result for ResumeMatch ID: {}. Starting full cleanup...", 
                resumeMatch.getId(), saveException);
            performFullCleanup(resumeMatch);
        }
    }
    
    private void performFullCleanup(ResumeMatch resumeMatch) {
        try {
            // Delete files from S3
            fileDeleteService.deleteResume(resumeMatch.getResumeUrl());
            fileDeleteService.deleteJobDescription(resumeMatch.getJobDescriptionUrl());
            log.info("Successfully deleted S3 files for ResumeMatch ID: {}", resumeMatch.getId());
            
            // Remove from user's history
            removeMatchFromUserHistory(resumeMatch);
            
            // Delete the ResumeMatch record
            resumeMatchRepository.deleteById(resumeMatch.getId());
            log.info("Successfully deleted ResumeMatch record ID: {}", resumeMatch.getId());
            
        } catch (Exception cleanupException) {
            log.error("Error during cleanup for ResumeMatch ID: {}. Manual cleanup may be required.", 
                resumeMatch.getId(), cleanupException);
        }
    }
    
    private void removeMatchFromUserHistory(ResumeMatch resumeMatch) {
        try {
            User user = userService.findById(resumeMatch.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found for ResumeMatch ID: " + resumeMatch.getId()));
            
            // Remove this match from the user's history
            user.removeFromHistory(resumeMatch);
        } catch (Exception e) {
            log.error("Failed to remove ResumeMatch ID: {} from user histories", resumeMatch.getId(), e);
        }
    }

    public ResumeMatch findById(String id) {
        return resumeMatchRepository.findById(id).orElse(null);
    }
    
    // Manual cleanup method for cases where async processing fails
    public void cleanupFailedMatch(String matchId) {
        try {
            ResumeMatch match = resumeMatchRepository.findById(matchId).orElse(null);
            if (match != null) {
                log.info("Starting manual cleanup for ResumeMatch ID: {}", matchId);
                performFullCleanup(match);
            } else {
                log.warn("ResumeMatch ID: {} not found for cleanup", matchId);
            }
        } catch (Exception e) {
            log.error("Error in manual cleanup for ResumeMatch ID: {}", matchId, e);
        }
    }
}
