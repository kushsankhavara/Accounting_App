package com.example.accounting.service;

import com.example.accounting.dto.*;
import com.example.accounting.model.Account;
import com.example.accounting.model.Transaction;
import com.example.accounting.model.TransactionType;
import com.example.accounting.repository.AccountRepository;
import com.example.accounting.repository.TransactionRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Account account = accountRepository.findByNameIgnoreCase(request.getAccount())
                .orElseGet(() -> accountRepository.save(new Account(request.getAccount(), "")));

        Transaction transaction = new Transaction();
        transaction.setDate(request.getDate());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setAccount(account);
        transaction.setNote(request.getNote());
        transaction.setPaymentMode(request.getPaymentMode());

        Transaction saved = transactionRepository.save(transaction);
        return new TransactionResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findTransactions(LocalDate startDate, LocalDate endDate, String category, String accountName, TransactionType type) {
        List<Transaction> results = transactionRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("category")), "%" + category.toLowerCase() + "%"));
            }
            if (accountName != null && !accountName.isBlank()) {
                predicates.add(cb.like(cb.lower(root.join("account").get("name")), "%" + accountName.toLowerCase() + "%"));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        return results.stream().map(TransactionResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MonthlySummary getMonthlySummary(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<Object[]> totals = transactionRepository.calculateMonthlyTotals(start, end);
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        if (!totals.isEmpty()) {
            Object[] row = totals.get(0);
            income = (BigDecimal) row[0];
            expense = (BigDecimal) row[1];
        }
        return new MonthlySummary(income, expense);
    }

    @Transactional(readOnly = true)
    public List<CategorySummary> getCategorySummary(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.sumByCategory(startDate, endDate)
                .stream()
                .map(row -> new CategorySummary((String) row[0], (BigDecimal) row[1]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccounts() {
        return accountRepository.findAll().stream().map(AccountResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        Optional<Account> existing = accountRepository.findByNameIgnoreCase(request.getName());
        if (existing.isPresent()) {
            Account account = existing.get();
            account.setDescription(request.getDescription());
            return new AccountResponse(accountRepository.save(account));
        }
        Account account = new Account(request.getName(), request.getDescription());
        return new AccountResponse(accountRepository.save(account));
    }

    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
