package com.example.accounting.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private UUID id;
    private LocalDate date;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private String account;
    private String note;
    private String paymentMode;

    public Transaction(UUID id, LocalDate date, BigDecimal amount, TransactionType type, String category, String account, String note, String paymentMode) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.account = account;
        this.note = note;
        this.paymentMode = paymentMode;
    }

    public UUID getId() {
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
