package com.divyanshu.Intellimatch.repository;

import com.divyanshu.Intellimatch.model.ResumeMatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeMatchRepository extends MongoRepository<ResumeMatch, String> {

}