package com.devsuv.account.application.service;

import com.devsuv.account.application.dto.AccountRequestDTO;
import com.devsuv.account.application.dto.AccountResponseDTO;
import com.devsuv.account.application.mapper.AccountMapper;
import com.devsuv.account.domain.exception.BusinessException;
import com.devsuv.account.domain.exception.ResourceNotFoundException;
import com.devsuv.account.domain.model.Account;
import com.devsuv.account.domain.port.AccountRepository;
import com.devsuv.account.domain.port.CustomerExistenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerExistenceRepository customerExistenceRepository;

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> findAll() {
        log.info("extrayendo todas las cuentas");
        return accountRepository.findAll().stream()
                .map(AccountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponseDTO findByAccountNumber(String accountNumber) {
        log.info("Fetching account with number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + accountNumber));
        return AccountMapper.toDTO(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> findByCustomerId(String customerId) {
        log.info("Fetching accounts for customerId: {}", customerId);
        return accountRepository.findByCustomerId(customerId).stream()
                .map(AccountMapper::toDTO)
                .collect(Collectors.toList());
    }

    public AccountResponseDTO create(AccountRequestDTO dto) {
        log.info("Creating account with number: {}", dto.getAccountNumber());

        if (!customerExistenceRepository.existsByCustomerIdAndStatusTrue(dto.getCustomerId())) {
            throw new BusinessException("No se encontró un cliente activo con customerId: " + dto.getCustomerId());
        }

        if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
            throw new BusinessException("Ya existe una cuenta con número: " + dto.getAccountNumber());
        }

        Account account = AccountMapper.toEntity(dto);
        Account saved = accountRepository.save(account);

        log.info("Account created successfully with number: {}", saved.getAccountNumber());
        return AccountMapper.toDTO(saved);
    }

    public AccountResponseDTO update(String accountNumber, AccountRequestDTO dto) {
        log.info("Updating account with number: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + accountNumber));

        if (dto.getCustomerId() != null && !customerExistenceRepository.existsByCustomerIdAndStatusTrue(dto.getCustomerId())) {
            throw new BusinessException("No se encontró un cliente activo con customerId: " + dto.getCustomerId());
        }

        AccountMapper.updateEntity(account, dto);
        Account saved = accountRepository.save(account);

        log.info("Account updated successfully with number: {}", saved.getAccountNumber());
        return AccountMapper.toDTO(saved);
    }

    public AccountResponseDTO patch(String accountNumber, AccountRequestDTO dto) {
        log.info("Patching account with number: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + accountNumber));

        if (dto.getCustomerId() != null && !customerExistenceRepository.existsByCustomerIdAndStatusTrue(dto.getCustomerId())) {
            throw new BusinessException("No se encontró un cliente activo con customerId: " + dto.getCustomerId());
        }

        AccountMapper.patchEntity(account, dto);
        Account saved = accountRepository.save(account);

        log.info("Account patched successfully with number: {}", saved.getAccountNumber());
        return AccountMapper.toDTO(saved);
    }

    public AccountResponseDTO delete(String accountNumber) {
        log.info("Deactivating account with number: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + accountNumber));

        account.setStatus(false);
        Account saved = accountRepository.save(account);

        log.info("Account deactivated successfully with number: {}", saved.getAccountNumber());
        return AccountMapper.toDTO(saved);
    }
}
