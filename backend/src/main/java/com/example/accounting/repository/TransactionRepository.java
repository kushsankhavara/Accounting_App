package com.example.accounting.repository;

import com.example.accounting.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT COALESCE(SUM(CASE WHEN t.type = com.example.accounting.model.TransactionType.INCOME THEN t.amount ELSE 0 END), 0) as income, " +
            "COALESCE(SUM(CASE WHEN t.type = com.example.accounting.model.TransactionType.EXPENSE THEN t.amount ELSE 0 END), 0) as expense " +
            "FROM Transaction t WHERE t.date BETWEEN :start AND :end")
    List<Object[]> calculateMonthlyTotals(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT t.category as category, COALESCE(SUM(t.amount), 0) as total " +
            "FROM Transaction t WHERE (:startDate IS NULL OR t.date >= :startDate) AND (:endDate IS NULL OR t.date <= :endDate) " +
            "GROUP BY t.category")
    List<Object[]> sumByCategory(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
