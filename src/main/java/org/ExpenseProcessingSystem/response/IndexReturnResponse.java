package org.ExpenseProcessingSystem.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexReturnResponse {
    private BigDecimal totalTransactionAmount;
    private BigDecimal totalCeiling;
    private List<SavingByDatesForIndex> savingsByDates;
}
