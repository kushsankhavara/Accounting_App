package com.example.accounting.controller;

import com.example.accounting.dto.CategorySummary;
import com.example.accounting.dto.MonthlySummary;
import com.example.accounting.dto.TransactionRequest;
import com.example.accounting.model.Transaction;
import com.example.accounting.model.TransactionType;
import com.example.accounting.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> addTransaction(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.add(request));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> listTransactions(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) TransactionType type
    ) {
        return ResponseEntity.ok(transactionService.find(startDate, endDate, category, account, type));
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<MonthlySummary> monthlySummary(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(transactionService.summarizeMonth(year, month));
    }

    @GetMapping("/summary/categories")
    public ResponseEntity<List<CategorySummary>> categorySummary(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(transactionService.summarizeByCategory(startDate, endDate));
    }

    @GetMapping(value = "/transactions/export", produces = "text/csv")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) TransactionType type
    ) {
        String csv = transactionService.exportCsv(startDate, endDate, category, account, type);
        byte[] data = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }
}
