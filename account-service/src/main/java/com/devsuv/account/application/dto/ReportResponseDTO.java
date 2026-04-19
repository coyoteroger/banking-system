package com.devsuv.account.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDTO {

    private String customerId;
    private String customerName;
    private List<AccountReportDTO> accounts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountReportDTO {
        private String accountNumber;
        private String accountType;
        private BigDecimal initialBalance;
        private Boolean status;
        private List<MovementReportDTO> movements;
    }
}
