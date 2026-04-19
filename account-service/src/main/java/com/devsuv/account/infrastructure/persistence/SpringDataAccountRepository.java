package com.devsuv.account.infrastructure.persistence;

import com.devsuv.account.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataAccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(String customerId);

    boolean existsByAccountNumber(String accountNumber);

    void deleteByAccountNumber(String accountNumber);
}
