package org.ExpenseProcessingSystem.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ExpenseProcessingSystem.response.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnsRequest {

    private Integer age;
    private BigDecimal wage;
    private BigDecimal inflation;

    private List<QPeriod> q;
    private List<PPeriod> p;
    private List<KPeriod> k;

    private List<Expense> transactions;
}