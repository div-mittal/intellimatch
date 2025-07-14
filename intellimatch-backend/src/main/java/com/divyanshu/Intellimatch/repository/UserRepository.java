package com.divyanshu.Intellimatch.repository;

import com.divyanshu.Intellimatch.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);

    // Additional methods can be defined here if needed
    User findByPhoneNumber(String phoneNumber);

    // I want to check if a user with the given email already exists
    boolean existsByEmail(String email);

    // I want to check if a user with the given phone number already exists
    boolean existsByPhoneNumber(String phoneNumber);

    // I want to check if a user is logged in by checking if a cookie with userId exists
    boolean existsById(@org.springframework.lang.NonNull String userId);
}