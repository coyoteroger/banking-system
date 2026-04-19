package com.devsuv.account.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDTO {

    @NotBlank(message = "El número de cuenta es requerido")
    private String accountNumber;

    @NotBlank(message = "El tipo de cuenta es requerido")
    @Pattern(regexp = "^(AHORROS|CORRIENTE)$", message = "El tipo de cuenta debe ser AHORROS o CORRIENTE")
    private String accountType;

    @NotNull(message = "El saldo inicial es requerido")
    @DecimalMin(value = "0.00", message = "El saldo inicial debe ser cero o positivo")
    private BigDecimal initialBalance;

    private Boolean status;

    @NotBlank(message = "El ID del cliente es requerido")
    private String customerId;
}
