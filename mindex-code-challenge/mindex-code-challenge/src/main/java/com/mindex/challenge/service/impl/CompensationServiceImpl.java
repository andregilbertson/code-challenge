package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationHistoryRepository;
import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.CompensationHistory;
import com.mindex.challenge.service.CompensationService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private CompensationHistoryRepository compensationHistoryRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        Compensation existingCompensation = compensationRepository.findByEmployeeId(compensation.getEmployeeId());

        //delete existing employee compensation if it exists
        if (existingCompensation != null) {
            updateCompensationHistory(existingCompensation);
            compensationRepository.deleteByEmployeeId(compensation.getEmployeeId());
        }

        //add compensation to database
        compensation.setCompensationId(UUID.randomUUID().toString());
        compensationRepository.save(compensation);

        return compensation;
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Reading compensation for employeeId [{}]", employeeId);

        Compensation compensation = compensationRepository.findByEmployeeId(employeeId);

        if (compensation == null) {
            throw new RuntimeException("No compensation found for employeeId: " + employeeId);
        }

        return compensation;
    } 

    @Override
    public CompensationHistory readHistory(String employeeId) {
        LOG.debug("Reading compensation history for employeeId [{}]", employeeId);

        CompensationHistory compensationHistory = compensationHistoryRepository.findByEmployeeId(employeeId);

        if (compensationHistory == null) {
            throw new RuntimeException("No compensation history found for employeeId: " + employeeId);
        }

        return compensationHistory;
    }

    private void updateCompensationHistory(Compensation oldCompensation) {
        //get current compensation history
        CompensationHistory compensationHistory = compensationHistoryRepository.findByEmployeeId(oldCompensation.getEmployeeId());

        //if it exists, add the oldCompensation to the history List and save to database.
        if (compensationHistory != null) {
            List<Compensation> history = compensationHistory.getHistory();
            history.add(oldCompensation);
            compensationHistory.setHistory(history);

            compensationHistoryRepository.save(compensationHistory);
        } 
        //else, create a new CompensationHistory and save to database
        else {
            CompensationHistory newCompensationHistory = new CompensationHistory();
            newCompensationHistory.setCompensationHistoryId(UUID.randomUUID().toString());
            newCompensationHistory.setEmployeeId(oldCompensation.getEmployeeId());
            List<Compensation> history = new ArrayList<Compensation>();
            history.add(oldCompensation);
            newCompensationHistory.setHistory(history);

            compensationHistoryRepository.save(newCompensationHistory);
        }
    }
}
