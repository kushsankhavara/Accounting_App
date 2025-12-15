package com.example.accounting.dto;

import com.example.accounting.model.Transaction;
import com.example.accounting.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionResponse {
    private Long id;
    private LocalDate date;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private String account;
    private String note;
    private String paymentMode;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.date = transaction.getDate();
        this.amount = transaction.getAmount();
        this.type = transaction.getType();
        this.category = transaction.getCategory();
        this.account = transaction.getAccount().getName();
        this.note = transaction.getNote();
        this.paymentMode = transaction.getPaymentMode();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getAccount() {
        return account;
    }

    public String getNote() {
        return note;
    }

    public String getPaymentMode() {
        return paymentMode;
    }
}
