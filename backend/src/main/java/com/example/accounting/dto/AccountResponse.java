package com.example.accounting.dto;

import com.example.accounting.model.Account;

public class AccountResponse {
    private Long id;
    private String name;
    private String description;

    public AccountResponse(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.description = account.getDescription();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
