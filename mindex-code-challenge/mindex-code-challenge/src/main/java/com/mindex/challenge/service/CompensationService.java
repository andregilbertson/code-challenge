package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.CompensationHistory;


public interface CompensationService {
    Compensation create(Compensation compensation);
    Compensation read(String employeeId);
    CompensationHistory readHistory(String employeeId);
}