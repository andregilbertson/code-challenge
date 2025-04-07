package com.mindex.challenge.dao;

import com.mindex.challenge.data.CompensationHistory;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface CompensationHistoryRepository extends MongoRepository<CompensationHistory, String> {
    CompensationHistory findByEmployeeId(String employeeId);
}