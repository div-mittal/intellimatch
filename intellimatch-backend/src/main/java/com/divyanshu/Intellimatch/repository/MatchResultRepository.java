package com.divyanshu.Intellimatch.repository;

import com.divyanshu.Intellimatch.model.MatchResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchResultRepository extends MongoRepository<MatchResult, String> {
    // Additional query methods can be defined here if needed
}
