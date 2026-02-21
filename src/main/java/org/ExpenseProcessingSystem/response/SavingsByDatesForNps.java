package org.ExpenseProcessingSystem.response;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class SavingsByDatesForNps {

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime start;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime end;

        private BigDecimal amount;
        private BigDecimal profit;
        private BigDecimal taxBenefit;
    }