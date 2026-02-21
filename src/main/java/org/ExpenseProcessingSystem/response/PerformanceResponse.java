package org.ExpenseProcessingSystem.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class PerformanceResponse {
        private String time;
        private String memory;
        private Integer threads;
    }