package org.ExpenseProcessingSystem.response;

import org.ExpenseProcessingSystem.enums.TransactionValidityType;

public class TransactionValidationResult {

    private final TransactionValidityType type;
    private final String message;

    public TransactionValidationResult(TransactionValidityType type, String message) {
        this.type = type;
        this.message = message;
    }

    public TransactionValidityType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public static TransactionValidationResult valid() {
        return new TransactionValidationResult(TransactionValidityType.VALID, null);
    }

    public static TransactionValidationResult invalid(String message) {
        return new TransactionValidationResult(TransactionValidityType.INVALID, message);
    }
}