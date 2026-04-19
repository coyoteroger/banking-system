package com.devsuv.account.domain.port;

import com.devsuv.account.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    List<Account> findAll();

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(String customerId);

    Account save(Account account);

    void deleteByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
