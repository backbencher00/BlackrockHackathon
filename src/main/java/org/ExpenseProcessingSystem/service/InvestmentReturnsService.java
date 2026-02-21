package org.ExpenseProcessingSystem.service;

import org.ExpenseProcessingSystem.enums.InvestmentType;
import org.ExpenseProcessingSystem.requests.ReturnsRequest;
import org.ExpenseProcessingSystem.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvestmentReturnsService {
    @Autowired
    ExpenseProcessingService service;
    public NpsReturnsResponse getNpsReturns(ReturnsRequest request){
        return getReturns(request, InvestmentType.NPS);
    }

//    public NpsReturnsResponse getIndexReturns(ReturnsRequest request){
//        return getReturns(request, InvestmentType.INDEX);
//    }

    public IndexReturnResponse getIndexReturns(ReturnsRequest request){
        List<Transaction> validTransactions = filterValidTransaction(request);

        BigDecimal totalCeiling = calculateTotalCeiling(validTransactions);
        BigDecimal totalTransactionAmount = calculateTotalTransactionAmount(validTransactions);

        List<SavingByDatesForIndex> savings =
                calculateSavingsByKPeriodsForIndex(validTransactions, request);

        return IndexReturnResponse.builder()
                .totalCeiling(totalCeiling)
                .totalTransactionAmount(totalTransactionAmount)
                .savingsByDates(savings)
                .build();
    }

    private List<SavingByDatesForIndex> calculateSavingsByKPeriodsForIndex(
            List<Transaction> validTransactions,
            ReturnsRequest request
    ) {

        List<SavingByDatesForIndex> result = new ArrayList<>();

        for (KPeriod k : request.getK()) {

            BigDecimal investedAmount = validTransactions.stream()
                    .filter(txn ->
                            !txn.getDate().isBefore(k.getStart()) &&
                                    !txn.getDate().isAfter(k.getEnd())
                    )
                    .map(Transaction::getRemanent)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (investedAmount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            BigDecimal realReturn = calculateIndexReturn(
                    investedAmount,
                    request.getAge(),
                    request.getInflation()
            );

            result.add(
                    SavingByDatesForIndex.builder()
                            .start(k.getStart())
                            .end(k.getEnd())
                            .returns(realReturn.setScale(1, RoundingMode.HALF_UP))
                            .build()
            );
        }

        return result;
    }




    public NpsReturnsResponse getReturns(ReturnsRequest request, InvestmentType type) {

        List<Transaction> validTransactions = filterValidTransaction(request);
        BigDecimal totalCeiling = calculateTotalCeiling(validTransactions);
        BigDecimal totalTransactionAmount = calculateTotalTransactionAmount(validTransactions);
        List<SavingsByDatesForNps> savingsByDates = calculateSavingsByKPeriods(validTransactions, request, type);
        return NpsReturnsResponse
                .builder()
                .totalCeiling(totalCeiling)
                .totalTransactionAmount(totalTransactionAmount)
                .savingsByDates(savingsByDates)
                .build();
    }
    public BigDecimal calculateTotalCeiling(List<Transaction> transactions){
        BigDecimal totalCeiling = BigDecimal.valueOf(0.00);
        for(Transaction txn : transactions){
            totalCeiling = totalCeiling.add(txn.getCeiling());
        }
        return totalCeiling.setScale(1);
    }

    public BigDecimal calculateTotalTransactionAmount(List<Transaction> transactions){
        BigDecimal totalTransactionAmount = BigDecimal.valueOf(0.00);
        System.out.println("----------------------------");
        for(Transaction txn : transactions){
            System.out.println(txn.getAmount());
            totalTransactionAmount = totalTransactionAmount.add(txn.getAmount());
        }
        return totalTransactionAmount.setScale(1);
    }


    private List<SavingsByDatesForNps> calculateSavingsByKPeriods(List<Transaction> validTransaction, ReturnsRequest request,
                                                                  InvestmentType type) {

        List<SavingsByDatesForNps> result = new ArrayList<>();

        for (KPeriod k : request.getK()) {

            BigDecimal investedAmount = validTransaction.stream()
                    .filter(txn ->
                            !txn.getDate().isBefore(k.getStart()) &&
                                    !txn.getDate().isAfter(k.getEnd())
                    )
                    .map(Transaction::getRemanent)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (investedAmount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            BigDecimal finalValue;

            if (type == InvestmentType.NPS) {
                finalValue = calculateNpsReturn(
                        investedAmount,
                        request.getAge(),
                        request.getInflation(),
                        request.getWage()
                );
            } else {
                finalValue = calculateIndexReturn(
                        investedAmount,
                        request.getAge(),
                        request.getInflation()
                );
            }

            BigDecimal profit = finalValue.subtract(investedAmount);

            BigDecimal taxBenefit =
                    (type == InvestmentType.NPS)
                            ? calculateTaxBenefit(investedAmount, request.getWage())
                            : BigDecimal.ZERO;

            result.add(
                    SavingsByDatesForNps.builder()
                            .start(k.getStart())
                            .end(k.getEnd())
                            .amount(investedAmount.setScale(1, RoundingMode.HALF_UP))
                            .profit(profit.setScale(2, RoundingMode.HALF_UP))
                            .taxBenefit(taxBenefit.setScale(2, RoundingMode.HALF_UP))
                            .build()
            );
        }

        return result;
    }

    private BigDecimal applyCompound(BigDecimal principal, BigDecimal annualRate, int years) {
        double result = principal.doubleValue() *
                Math.pow(1 + annualRate.doubleValue(), years);

        return BigDecimal.valueOf(result);
    }

    private BigDecimal adjustForInflation(BigDecimal amount, BigDecimal inflationRate, int years) {
        double adjusted =
                amount.doubleValue() /
                        Math.pow(1 + inflationRate.doubleValue(), years);

        return BigDecimal.valueOf(adjusted);
    }

    private BigDecimal calculateNpsReturn(BigDecimal invested, int age, BigDecimal inflation, BigDecimal wage) {

        int years = calculateYearsUntilRetirement(age);

        BigDecimal rate = BigDecimal.valueOf(0.0711);

        BigDecimal compounded =
                applyCompound(invested, rate, years);

        return adjustForInflation(
                compounded,
                inflation.divide(BigDecimal.valueOf(100)),
                years
        );
    }

    private BigDecimal calculateIndexReturn(BigDecimal invested, int age, BigDecimal inflation) {

        int years = calculateYearsUntilRetirement(age);

        BigDecimal rate = BigDecimal.valueOf(0.1449);

        BigDecimal compounded =
                applyCompound(invested, rate, years);

        return adjustForInflation(
                compounded,
                inflation.divide(BigDecimal.valueOf(100)),
                years
        );
    }

    private int calculateYearsUntilRetirement(int age) {
        int retirementAge = 60;
        return age < retirementAge ? retirementAge - age : 5;
    }



    private BigDecimal calculateTaxBenefit(
            BigDecimal invested,
            BigDecimal wage
    ) {

        BigDecimal annualIncome =
                wage.multiply(BigDecimal.valueOf(12));

        BigDecimal tenPercent =
                annualIncome.multiply(BigDecimal.valueOf(0.10));

        BigDecimal maxLimit =
                BigDecimal.valueOf(200000);

        BigDecimal deduction =
                invested.min(tenPercent).min(maxLimit);

        BigDecimal taxBefore =
                calculateTax(annualIncome);

        BigDecimal taxAfter =
                calculateTax(annualIncome.subtract(deduction));

        return taxBefore.subtract(taxAfter);
    }


    private BigDecimal calculateTax(BigDecimal income) {

        double inc = income.doubleValue();

        if (inc <= 700000) return BigDecimal.ZERO;

        if (inc <= 1000000)
            return BigDecimal.valueOf((inc - 700000) * 0.10);

        if (inc <= 1200000)
            return BigDecimal.valueOf(30000 +
                    (inc - 1000000) * 0.15);

        if (inc <= 1500000)
            return BigDecimal.valueOf(60000 +
                    (inc - 1200000) * 0.20);

        return BigDecimal.valueOf(120000 +
                (inc - 1500000) * 0.30);
    }

    public List<Transaction> filterValidTransaction(ReturnsRequest request){
        List<Transaction> transactions =  service.parseTransactions(request.getTransactions());

        TransactionValidatorResponse validatorResponse = service.validateTransactions(TransactionValidatorRequest.builder()
                .transactions(transactions).wage(request.getWage())
                .build());

        List<Transaction> valid = validatorResponse.getValid();

        //q must run first because  p will be added on top of q
        service.applyQMoment(request.getQ(), valid);
        service.applyPMoment(request.getP(), valid);
        service.applyK(request.getK(), valid);
        for (Transaction transaction : valid){
            System.out.println(transaction.toString());
        }

        return valid;
    }



}
