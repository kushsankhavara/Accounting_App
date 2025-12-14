package com.example.accounting;

import com.example.accounting.dto.MonthlySummary;
import com.example.accounting.dto.TransactionRequest;
import com.example.accounting.model.TransactionType;
import com.example.accounting.service.TransactionService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionServiceTest {

    private final TransactionService service = new TransactionService();

    @Test
    void calculatesMonthlySummary() {
        TransactionRequest income = new TransactionRequest();
        income.setAmount(new BigDecimal("1000"));
        income.setCategory("Salary");
        income.setAccount("Checking");
        income.setDate(LocalDate.of(2024, 10, 1));
        income.setType(TransactionType.INCOME);
        income.setPaymentMode("Bank");

        TransactionRequest expense = new TransactionRequest();
        expense.setAmount(new BigDecimal("200"));
        expense.setCategory("Food");
        expense.setAccount("Checking");
        expense.setDate(LocalDate.of(2024, 10, 2));
        expense.setType(TransactionType.EXPENSE);
        expense.setPaymentMode("Card");

        service.add(income);
        service.add(expense);

        MonthlySummary summary = service.summarizeMonth(2024, 10);
        assertThat(summary.getTotalIncome()).isEqualByComparingTo("1000");
        assertThat(summary.getTotalExpense()).isEqualByComparingTo("200");
        assertThat(summary.getBalance()).isEqualByComparingTo("800");
    }
}
