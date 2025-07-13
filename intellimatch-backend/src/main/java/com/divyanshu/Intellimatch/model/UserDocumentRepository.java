package com.divyanshu.Intellimatch.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.divyanshu.Intellimatch.repository.UserDocument;

@Repository
public interface UserDocumentRepository extends MongoRepository<UserDocument, String> {}