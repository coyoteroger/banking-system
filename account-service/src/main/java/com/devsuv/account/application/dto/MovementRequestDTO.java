package com.devsuv.account.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementRequestDTO {

    @NotBlank(message = "El número de cuenta es requerido")
    private String accountNumber;

    @NotNull(message = "El valor es requerido")
    @DecimalMin(value = "-999999999.99", message = "El valor está fuera del rango permitido")
    @DecimalMax(value = "999999999.99", message = "El valor está fuera del rango permitido")
    private BigDecimal value;
}
