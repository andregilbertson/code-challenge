package com.mindex.challenge.service.impl;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Reading reporting structure for employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        //create new ReportingStructure
        ReportingStructure reportingStructure = new ReportingStructure();

        //set employee
        reportingStructure.setEmployee(employee);

        //calculate and set numberOfReports
        int numberOfReports = calculateNumberOfReports(employee, new HashSet<>());
        reportingStructure.setNumberOfReports(numberOfReports);
        
        //return
        return reportingStructure;

    }

    private int calculateNumberOfReports(Employee employee, Set<String> seenEmployees) {
        //base cases: employee null/DNE or employee has no direct reports
        if (employee == null || employee.getDirectReports() == null) {
            return 0;
        }

        //check for circular reporting using String Set
        String employeeId = employee.getEmployeeId();
        if (seenEmployees.contains(employeeId)) {
            LOG.warn("Circular reporting detected for employee with id [{}].", employeeId);
            //must return -1 or else an employee can count in its own numberOfReports (illogical)
            return -1;
        }
        seenEmployees.add(employeeId);

        //recursively calculate numberOfReports
        int numberOfReports = 0;
        for (Employee directReport : employee.getDirectReports()) {
            Employee fullDirectReport = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
            numberOfReports += 1 + calculateNumberOfReports(fullDirectReport, seenEmployees);
        }

        //return
        return numberOfReports;
    }
}