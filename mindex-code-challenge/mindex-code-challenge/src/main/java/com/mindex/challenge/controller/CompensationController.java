package com.mindex.challenge.controller;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.CompensationHistory;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for [{}]", compensation);

        return compensationService.create(compensation);
    }

    @GetMapping("/compensation/{id}")
    public Compensation read(@PathVariable String id) {
        LOG.debug("Received compensation read request for employee with id [{}]", id);

        return compensationService.read(id);
    }

    @GetMapping("/compensation/{id}/history")
    public CompensationHistory readHistory(@PathVariable String id) {
        LOG.debug("Received compensation history read request for employee with id [{}]", id);

        return compensationService.readHistory(id);
    }
}