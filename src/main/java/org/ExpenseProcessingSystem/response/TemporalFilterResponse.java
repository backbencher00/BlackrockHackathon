package org.ExpenseProcessingSystem.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporalFilterResponse {
    private List<Transaction> valid;
    private List<InvalidFilteredTransaction> invalid;
}