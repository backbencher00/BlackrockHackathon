package org.ExpenseProcessingSystem.controller;

import org.ExpenseProcessingSystem.response.PerformanceResponse;
import org.ExpenseProcessingSystem.service.SystemPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class HealthCheckController {
    @Autowired
    private SystemPerformanceService performanceService;

    @GetMapping("/performance")
    public PerformanceResponse getPerformance() {
        return performanceService.getPerformanceMetrics();
    }
}
