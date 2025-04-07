package com.mindex.challenge.data;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class CompensationHistory {
    @Id
    private String compensationHistoryId;
    @Indexed(unique = true)
    private String employeeId;
    private List<Compensation> history;

    public CompensationHistory() {
    }
    
    public String getCompensationHistoryId() {
        return compensationHistoryId;
    }

    public void setCompensationHistoryId(String compensationHistoryId) {
        this.compensationHistoryId = compensationHistoryId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public List<Compensation> getHistory() {
        return history;
    }

    public void setHistory(List<Compensation> history) {
        this.history = history;
    }
}
