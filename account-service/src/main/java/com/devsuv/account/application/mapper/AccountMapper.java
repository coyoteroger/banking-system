package com.devsuv.account.application.mapper;

import com.devsuv.account.application.dto.AccountRequestDTO;
import com.devsuv.account.application.dto.AccountResponseDTO;
import com.devsuv.account.domain.model.Account;

public final class AccountMapper {

    private AccountMapper() {
    }

    public static Account toEntity(AccountRequestDTO dto) {
        return Account.builder()
                .accountNumber(dto.getAccountNumber())
                .accountType(dto.getAccountType())
                .initialBalance(dto.getInitialBalance())
                .status(true)
                .customerId(dto.getCustomerId())
                .build();
    }

    public static AccountResponseDTO toDTO(Account account) {
        return AccountResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .initialBalance(account.getInitialBalance())
                .status(account.getStatus())
                .customerId(account.getCustomerId())
                .createdAt(account.getCreatedAt())
                .build();
    }

    public static void updateEntity(Account account, AccountRequestDTO dto) {
        account.setAccountType(dto.getAccountType());
        account.setInitialBalance(dto.getInitialBalance());
        if (dto.getStatus() != null) account.setStatus(dto.getStatus());
        account.setCustomerId(dto.getCustomerId());
    }

    public static void patchEntity(Account account, AccountRequestDTO dto) {
        if (dto.getAccountType() != null) account.setAccountType(dto.getAccountType());
        if (dto.getInitialBalance() != null) account.setInitialBalance(dto.getInitialBalance());
        if (dto.getStatus() != null) account.setStatus(dto.getStatus());
        if (dto.getCustomerId() != null) account.setCustomerId(dto.getCustomerId());
    }
}
