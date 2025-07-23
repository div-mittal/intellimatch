package com.divyanshu.Intellimatch.service;

import com.divyanshu.Intellimatch.model.MatchResult;
import com.divyanshu.Intellimatch.model.ResumeMatch;
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

    private final NlpAnalysisService nlpAnalysisService;
    private final MatchResultRepository matchResultRepository;
    private final ResumeMatchRepository resumeMatchRepository;

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

            // Save the MatchResult
            MatchResult savedMatchResult = matchResultRepository.save(matchResult);
            
            // Update the ResumeMatch with the MatchResult ID
            resumeMatch.setMatchResultId(savedMatchResult.getId());
            resumeMatchRepository.save(resumeMatch);
            
            log.info("Analysis completed for ResumeMatch ID: {}, MatchResult ID: {}", 
                resumeMatch.getId(), savedMatchResult.getId());
                
        } catch (Exception e) {
            log.error("Error processing match analysis for ResumeMatch ID: {}", resumeMatch.getId(), e);
        }
    }

    public ResumeMatch findById(String id) {
        return resumeMatchRepository.findById(id).orElse(null);
    }
}
