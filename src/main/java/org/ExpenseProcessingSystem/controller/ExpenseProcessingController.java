package org.ExpenseProcessingSystem.controller;

import org.ExpenseProcessingSystem.requests.TemporalFilterRequest;
import org.ExpenseProcessingSystem.response.*;
import org.ExpenseProcessingSystem.service.ExpenseProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class ExpenseProcessingController {

    @Autowired
    ExpenseProcessingService expenseProcessingService;



    @PostMapping("/transactions:parse")
    public ParseTransactionsResponse parseTransactions(@RequestBody ParseTransactionsRequest request) {
        return ParseTransactionsResponse.builder()
                .transactions(
                        expenseProcessingService.parseTransactions(request.getExpenses())
                ).build();
    }

    @PostMapping("/transactions:validator")
    public TransactionValidatorResponse validateTransactions(
            @RequestBody TransactionValidatorRequest request) {

        return expenseProcessingService.validateTransactions(request);
    }

    @PostMapping("/transactions:filter")
    public TemporalFilterResponse filterTransactions(
            @RequestBody TemporalFilterRequest request) {
        return expenseProcessingService.filterTransactions(request);
    }
}
