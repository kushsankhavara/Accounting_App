package com.example.accounting.service;

import com.example.accounting.dto.CategorySummary;
import com.example.accounting.dto.MonthlySummary;
import com.example.accounting.dto.TransactionRequest;
import com.example.accounting.model.Transaction;
import com.example.accounting.model.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final Map<UUID, Transaction> transactions = new ConcurrentHashMap<>();

    public Transaction add(TransactionRequest request) {
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction(
                id,
                request.getDate(),
                request.getAmount(),
                request.getType(),
                request.getCategory(),
                request.getAccount(),
                request.getNote(),
                request.getPaymentMode()
        );
        transactions.put(id, transaction);
        return transaction;
    }

    public List<Transaction> find(LocalDate startDate, LocalDate endDate, String category, String account, TransactionType type) {
        return transactions.values().stream()
                .filter(tx -> startDate == null || !tx.getDate().isBefore(startDate))
                .filter(tx -> endDate == null || !tx.getDate().isAfter(endDate))
                .filter(tx -> category == null || category.equalsIgnoreCase(tx.getCategory()))
                .filter(tx -> account == null || account.equalsIgnoreCase(tx.getAccount()))
                .filter(tx -> type == null || tx.getType() == type)
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public MonthlySummary summarizeMonth(int year, int month) {
        YearMonth target = YearMonth.of(year, month);
        List<Transaction> monthTransactions = transactions.values().stream()
                .filter(tx -> YearMonth.from(tx.getDate()).equals(target))
                .toList();
        BigDecimal income = calculateTotal(monthTransactions, TransactionType.INCOME);
        BigDecimal expense = calculateTotal(monthTransactions, TransactionType.EXPENSE);
        return new MonthlySummary(income, expense);
    }

    public List<CategorySummary> summarizeByCategory(LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> totals = new ConcurrentHashMap<>();
        find(startDate, endDate, null, null, null).forEach(tx -> {
            totals.merge(tx.getCategory(), tx.getAmount(), BigDecimal::add);
        });
        List<CategorySummary> summaries = new ArrayList<>();
        totals.forEach((category, total) -> summaries.add(new CategorySummary(category, total)));
        summaries.sort(Comparator.comparing(CategorySummary::getTotal).reversed());
        return summaries;
    }

    public String exportCsv(LocalDate startDate, LocalDate endDate, String category, String account, TransactionType type) {
        String header = "id,date,amount,type,category,account,note,paymentMode";
        List<String> rows = find(startDate, endDate, category, account, type).stream()
                .map(tx -> String.join(",",
                        tx.getId().toString(),
                        tx.getDate().toString(),
                        tx.getAmount().toPlainString(),
                        tx.getType().name(),
                        escape(tx.getCategory()),
                        escape(tx.getAccount()),
                        escape(tx.getNote()),
                        escape(tx.getPaymentMode())))
                .toList();
        List<String> csv = new ArrayList<>();
        csv.add(header);
        csv.addAll(rows);
        return String.join("\n", csv);
    }

    private BigDecimal calculateTotal(List<Transaction> items, TransactionType type) {
        return items.stream()
                .filter(tx -> tx.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String sanitized = value.replace("\"", "\"\"");
        if (sanitized.contains(",")) {
            return "\"" + sanitized + "\"";
        }
        return sanitized;
    }
}
