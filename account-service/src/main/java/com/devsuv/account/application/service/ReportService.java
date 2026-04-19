package com.devsuv.account.application.service;

import com.devsuv.account.application.dto.MovementReportDTO;
import com.devsuv.account.application.dto.ReportResponseDTO;
import com.devsuv.account.domain.model.Account;
import com.devsuv.account.domain.model.Movement;
import com.devsuv.account.domain.port.AccountRepository;
import com.devsuv.account.domain.port.CustomerInfoRepository;
import com.devsuv.account.domain.port.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final CustomerInfoRepository customerInfoRepository;

    public ReportResponseDTO generateReport(String customerId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating report for customerId: {} from {} to {}", customerId, startDate, endDate);

        String customerName = customerInfoRepository.findByCustomerId(customerId)
                .map(c -> c.getName())
                .orElse("N/A");

        List<Account> accounts = accountRepository.findByCustomerId(customerId);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<ReportResponseDTO.AccountReportDTO> accountReports = accounts.stream()
                .map(account -> buildAccountReport(account, startDateTime, endDateTime))
                .collect(Collectors.toList());

        log.info("Report generated with {} accounts for customerId: {}", accountReports.size(), customerId);

        return ReportResponseDTO.builder()
                .customerId(customerId)
                .customerName(customerName)
                .accounts(accountReports)
                .build();
    }

    private ReportResponseDTO.AccountReportDTO buildAccountReport(Account account,
                                                                   LocalDateTime startDateTime,
                                                                   LocalDateTime endDateTime) {
        List<Movement> movements = movementRepository.findByAccountAccountNumberAndDateBetween(
                account.getAccountNumber(), startDateTime, endDateTime);

        List<MovementReportDTO> movementDTOs = movements.stream()
                .map(m -> MovementReportDTO.builder()
                        .date(m.getDate())
                        .movementType(m.getMovementType())
                        .value(m.getValue())
                        .balance(m.getBalance())
                        .status(account.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ReportResponseDTO.AccountReportDTO.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .initialBalance(account.getInitialBalance())
                .status(account.getStatus())
                .movements(movementDTOs)
                .build();
    }
}
