package com.example.accounting.dto;

import java.math.BigDecimal;

public class CategorySummary {
    private String category;
    private BigDecimal total;

    public CategorySummary(String category, BigDecimal total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
