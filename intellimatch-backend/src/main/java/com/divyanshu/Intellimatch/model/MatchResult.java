package com.divyanshu.Intellimatch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "match_results")
public class MatchResult {
    @Id
    private String id;
    private int atsScorePercent;
    private String summary;
    private List<MatchDetail> whatMatched;
    private List<MissingDetail> whatIsMissing;

    @Data
    public static class MatchDetail {
        private String item;
        private String reason;
    }

    @Data
    public static class MissingDetail {
        private String item;
        private String recommendation;
    }
}
