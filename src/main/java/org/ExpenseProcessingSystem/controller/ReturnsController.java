package org.ExpenseProcessingSystem.controller;

import org.ExpenseProcessingSystem.requests.ReturnsRequest;
import org.ExpenseProcessingSystem.response.NpsReturnsResponse;
import org.ExpenseProcessingSystem.service.InvestmentReturnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/blackrock/challenge/v1")
public class ReturnsController {

    @Autowired
    InvestmentReturnsService investmentReturnsService;

    @PostMapping("/returns:nps")
    public NpsReturnsResponse getNpsReturns(@RequestBody ReturnsRequest request) {
        return investmentReturnsService.getNpsReturns(request);
    }

    @PostMapping("/returns:index")
    public NpsReturnsResponse getIndexReturns(@RequestBody ReturnsRequest request) {

        return investmentReturnsService.getIndexReturns(request);
    }
}
