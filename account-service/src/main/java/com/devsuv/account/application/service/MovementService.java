package com.devsuv.account.application.service;

import com.devsuv.account.application.dto.MovementRequestDTO;
import com.devsuv.account.application.dto.MovementResponseDTO;
import com.devsuv.account.application.mapper.MovementMapper;
import com.devsuv.account.domain.exception.BusinessException;
import com.devsuv.account.domain.exception.InsufficientBalanceException;
import com.devsuv.account.domain.exception.ResourceNotFoundException;
import com.devsuv.account.domain.model.Account;
import com.devsuv.account.domain.model.Movement;
import com.devsuv.account.domain.port.AccountRepository;
import com.devsuv.account.domain.port.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovementService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<MovementResponseDTO> findAll() {
        log.info("Fetching all movements");
        return movementRepository.findAll().stream()
                .map(MovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovementResponseDTO findById(Long id) {
        log.info("Fetching movement with id: {}", id);
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
        return MovementMapper.toDTO(movement);
    }

    @Transactional(readOnly = true)
    public List<MovementResponseDTO> findByAccountNumber(String accountNumber) {
        log.info("Fetching movements for account: {}", accountNumber);
        return movementRepository.findByAccountAccountNumber(accountNumber).stream()
                .map(MovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    
    public MovementResponseDTO create(MovementRequestDTO dto) {
        log.info("Creating movement for account: {} with value: {}", dto.getAccountNumber(), dto.getValue());

        Account account = accountRepository.findByAccountNumber(dto.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta no encontrada con número: " + dto.getAccountNumber()));

        if (!Boolean.TRUE.equals(account.getStatus())) {
            throw new BusinessException("La cuenta " + dto.getAccountNumber() + " se encuentra inactiva");
        }

        BigDecimal currentBalance = getCurrentBalance(account);

        BigDecimal newBalance = currentBalance.add(dto.getValue());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Insufficient balance for account: {}. Current: {}, Requested: {}",
                    dto.getAccountNumber(), currentBalance, dto.getValue());
            throw new InsufficientBalanceException("Saldo no disponible");
        }

        String movementType = dto.getValue().compareTo(BigDecimal.ZERO) >= 0 ? "Deposito" : "Retiro";

        Movement movement = Movement.builder()
                .date(LocalDateTime.now())
                .movementType(movementType)
                .value(dto.getValue())
                .balance(newBalance)
                .account(account)
                .build();

        Movement saved = movementRepository.save(movement);

        log.info("Movement created successfully. Account: {}, Type: {}, Value: {}, New Balance: {}",
                dto.getAccountNumber(), movementType, dto.getValue(), newBalance);

        return MovementMapper.toDTO(saved);
    }

   
    private BigDecimal getCurrentBalance(Account account) {
        return movementRepository
                .findTopByAccountAccountNumberOrderByDateDesc(account.getAccountNumber())
                .map(Movement::getBalance)
                .orElse(account.getInitialBalance());
    }
}
