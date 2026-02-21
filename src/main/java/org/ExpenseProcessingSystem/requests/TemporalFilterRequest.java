package org.ExpenseProcessingSystem.requests;

import lombok.*;
import org.ExpenseProcessingSystem.response.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporalFilterRequest {

    private List<QPeriod> q;
    private List<PPeriod> p;
    private List<KPeriod> k;

    private BigDecimal wage;

    private List<Expense> transactions;

}
