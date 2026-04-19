package com.devsuv.account.infrastructure.persistence;

import com.devsuv.account.domain.model.Account;
import com.devsuv.account.domain.port.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountJpaAdapter implements AccountRepository {

    private final SpringDataAccountRepository springDataRepository;

    @Override
    public List<Account> findAll() {
        return springDataRepository.findAll();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return springDataRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> findByCustomerId(String customerId) {
        return springDataRepository.findByCustomerId(customerId);
    }

    @Override
    public Account save(Account account) {
        return springDataRepository.save(account);
    }

    @Override
    public void deleteByAccountNumber(String accountNumber) {
        springDataRepository.deleteByAccountNumber(accountNumber);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return springDataRepository.existsByAccountNumber(accountNumber);
    }
}
