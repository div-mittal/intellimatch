package com.divyanshu.Intellimatch.service;

import com.divyanshu.Intellimatch.model.MatchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NlpAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${nlp.api.url}")
    private String nlpApiUrl;

    public MatchResult analyzeResumeMatch(String resumeUrl, String jobDescriptionUrl) {
        try {
            // Prepare request payload
            Map<String, String> payload = new HashMap<>();
            payload.put("resumeUrl", resumeUrl);
            payload.put("jobDescriptionUrl", jobDescriptionUrl);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request entity
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);


            // Make API call
            ResponseEntity<String> response = restTemplate.exchange(
                nlpApiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                
                return parseNlpResponse(responseBody);
            } else {
                return createDefaultErrorResult();
            }

        } catch (Exception e) {
            return createDefaultErrorResult();
        }
    }

    private MatchResult parseNlpResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            MatchResult matchResult = new MatchResult();
            matchResult.setAtsScorePercent(jsonNode.get("ats_score_percent").asInt());
            matchResult.setSummary(jsonNode.get("summary").asText());

            // Parse what_matched array
            List<MatchResult.MatchDetail> whatMatched = new ArrayList<>();
            JsonNode whatMatchedNode = jsonNode.get("what_matched");
            if (whatMatchedNode != null && whatMatchedNode.isArray()) {
                for (JsonNode matchNode : whatMatchedNode) {
                    MatchResult.MatchDetail detail = new MatchResult.MatchDetail();
                    detail.setItem(matchNode.get("item").asText());
                    detail.setReason(matchNode.get("reason").asText());
                    whatMatched.add(detail);
                }
            }
            matchResult.setWhatMatched(whatMatched);

            // Parse what_is_missing array
            List<MatchResult.MissingDetail> whatIsMissing = new ArrayList<>();
            JsonNode whatIsMissingNode = jsonNode.get("what_is_missing");
            if (whatIsMissingNode != null && whatIsMissingNode.isArray()) {
                for (JsonNode missingNode : whatIsMissingNode) {
                    MatchResult.MissingDetail detail = new MatchResult.MissingDetail();
                    detail.setItem(missingNode.get("item").asText());
                    detail.setRecommendation(missingNode.get("recommendation").asText());
                    whatIsMissing.add(detail);
                }
            }
            matchResult.setWhatIsMissing(whatIsMissing);

            return matchResult;

        } catch (Exception e) {
            return createDefaultErrorResult();
        }
    }

    private MatchResult createDefaultErrorResult() {
        MatchResult errorResult = new MatchResult();
        errorResult.setAtsScorePercent(0);
        errorResult.setSummary("Analysis failed. Please try again later.");
        errorResult.setWhatMatched(new ArrayList<>());
        errorResult.setWhatIsMissing(new ArrayList<>());
        return errorResult;
    }
}
