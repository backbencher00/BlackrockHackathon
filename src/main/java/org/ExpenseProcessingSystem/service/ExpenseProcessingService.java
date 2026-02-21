package org.ExpenseProcessingSystem.service;

import org.ExpenseProcessingSystem.enums.TransactionValidityType;
import org.ExpenseProcessingSystem.requests.TemporalFilterRequest;
import org.ExpenseProcessingSystem.response.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseProcessingService {

    public List<Transaction> parseTransactions(List<Expense> expenses) {
        return expenses
                        .stream()
                        .map(this::calculateCeilingAndRemanent)
                        .toList();
    }


    private Transaction calculateCeilingAndRemanent(Expense expense) {

        BigDecimal amount = expense.getAmount();

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        BigDecimal hundred = BigDecimal.valueOf(100);

        // Divide by 100 and round UP to next integer
        BigDecimal multiplier = amount.divide(hundred, 0, RoundingMode.CEILING);

        // Multiply back by 100
        BigDecimal ceiling = multiplier.multiply(hundred);

        BigDecimal remanent = ceiling.subtract(amount);

        return Transaction.builder()
                .date(expense.getDate())
                .amount(amount)
                .ceiling(ceiling)
                .remanent(remanent)
                .build();
    }


    public TransactionValidatorResponse validateTransactions(
            TransactionValidatorRequest request) {

        List<Transaction> validList = new ArrayList<>();
        List<InvalidTransaction> invalidList = new ArrayList<>();

        Set<String> seenTransactions = new HashSet<>();
        BigDecimal wage = request.getWage();

        for (Transaction tx : request.getTransactions()) {
            TransactionValidationResult result = returnTransactionValidity(tx, wage, seenTransactions);
            Transaction transactionResponse = Transaction.builder()
                    .date(tx.getDate())
                    .amount(tx.getAmount().setScale(1, RoundingMode.HALF_UP))
                    .ceiling(tx.getCeiling().setScale(1, RoundingMode.HALF_UP))
                    .remanent(tx.getRemanent().setScale(1, RoundingMode.HALF_UP))
                    .build();
            if (result.getType() == TransactionValidityType.VALID) {
                validList.add(transactionResponse);

            } else {
                invalidList.add(
                        new InvalidTransaction(
                                transactionResponse.getDate(),
                                transactionResponse.getAmount(),
                                transactionResponse.getCeiling(),
                                transactionResponse.getRemanent(),
                                result.getMessage()
                        )
                );
            }
        }

        return TransactionValidatorResponse.builder()
                .valid(validList)
                .invalid(invalidList)
                .build();
    }

    public TransactionValidationResult returnTransactionValidity(Transaction tx, BigDecimal wage, Set<String> seenTransactions) {
        if (tx.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return TransactionValidationResult.invalid("Negative amounts are not allowed");
        }

        if (tx.getAmount().compareTo(wage) > 0) {
            return TransactionValidationResult.invalid("Expense amount is greater than wages");
        }

        String key = tx.getDate().toString()
                + "|" + tx.getAmount().stripTrailingZeros().toPlainString()
                + "|" + tx.getCeiling().stripTrailingZeros().toPlainString()
                + "|" + tx.getRemanent().stripTrailingZeros().toPlainString();

        if (!seenTransactions.add(key)) {
            return TransactionValidationResult.invalid("Duplicate transaction");
        }

        return TransactionValidationResult.valid();
    }



    public TemporalFilterResponse filterTransactions(TemporalFilterRequest request) {
        List<Transaction> transactions =  parseTransactions(request.getTransactions());

        TransactionValidatorResponse validatorResponse = validateTransactions(TransactionValidatorRequest.builder()
                .transactions(transactions).wage(request.getWage())
                .build());

        List<Transaction> valid = validatorResponse.getValid();


        //q must run first because  p will be added on top of q
        applyQMoment(request.getQ(), valid);
        applyPMoment(request.getP(), valid);
        applyK(request.getK(),valid);
        removeIfRemanentIsZero(valid);

        List<InvalidTransaction> invalid = validatorResponse.getInvalid();

        return TemporalFilterResponse.builder().valid(valid).invalid(mapToFiltered(invalid)).build();

    }

    public List<InvalidFilteredTransaction> mapToFiltered(List<InvalidTransaction> invalidTransactions) {

        if (invalidTransactions == null) {
            return Collections.emptyList();
        }

        return invalidTransactions.stream()
                .map(txn -> InvalidFilteredTransaction.builder()
                        .date(txn.getDate())
                        .amount(txn.getAmount())
                        .message(txn.getMessage())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public void applyQMoment(List<QPeriod> qPeriods, List<Transaction> transactions) {

        if (qPeriods == null || qPeriods.isEmpty()) {
            return;
        }

        for (Transaction transaction : transactions) {

            LocalDateTime txnDate = transaction.getDate();

            QPeriod matchedQ = qPeriods.stream()
                    .filter(q ->
                            !txnDate.isBefore(q.getStart()) &&
                                    !txnDate.isAfter(q.getEnd())
                    )
                    // latest start date wins
                    .max(Comparator.comparing(QPeriod::getStart))
                    .orElse(null);

            if (matchedQ != null) {
                transaction.setRemanent(
                        matchedQ.getFixed().setScale(1)
                );
            }
        }
    }

    public void applyPMoment(List<PPeriod> pPeriods, List<Transaction> transactions) {

        if (pPeriods == null || pPeriods.isEmpty()) {
            return;
        }

        for (Transaction transaction : transactions) {

            LocalDateTime txnDate = transaction.getDate();

            BigDecimal totalExtra = pPeriods.stream()
                    .filter(p ->
                            !txnDate.isBefore(p.getStart()) &&
                                    !txnDate.isAfter(p.getEnd())
                    )
                    .map(PPeriod::getExtra)   // must return BigDecimal
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalExtra.compareTo(BigDecimal.ZERO) > 0) {

                transaction.setRemanent(
                        transaction.getRemanent()
                                .add(totalExtra)
                                .setScale(1)
                );
            }
        }
    }
    

    public void applyK(List<KPeriod> kPeriods, List<Transaction> valid) {
        if (kPeriods == null || kPeriods.isEmpty()) {
            valid.clear();   // no investment period defined
            return;
        }
        valid.removeIf(transaction ->
                // remove if NOT in K OR remanent <= 0
                !inKPeriod(transaction, kPeriods)
        );
    }

    public void removeIfRemanentIsZero(List<Transaction> valid){
        valid.removeIf(transaction ->
                // remove if NOT in K OR remanent <= 0
                transaction.getRemanent().compareTo(BigDecimal.ZERO) <= 0
        );
    }

    public boolean inKPeriod(Transaction transaction, List<KPeriod> kPeriods) {

        if (kPeriods == null || kPeriods.isEmpty()) {
            return false;
        }

        LocalDateTime txnDate = transaction.getDate();

        return kPeriods.stream()
                .anyMatch(k ->
                        !txnDate.isBefore(k.getStart()) &&
                                !txnDate.isAfter(k.getEnd())
                );
    }
}
