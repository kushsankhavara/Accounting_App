package com.example.accounting.repository;

import com.example.accounting.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByNameIgnoreCase(String name);
}
