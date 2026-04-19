package com.devsuv.account.application.service;

import com.devsuv.account.application.dto.MovementRequestDTO;
import com.devsuv.account.application.dto.MovementResponseDTO;
import com.devsuv.account.domain.exception.InsufficientBalanceException;
import com.devsuv.account.domain.exception.ResourceNotFoundException;
import com.devsuv.account.domain.model.Account;
import com.devsuv.account.domain.model.Movement;
import com.devsuv.account.domain.port.AccountRepository;
import com.devsuv.account.domain.port.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private MovementService movementService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .accountNumber("478758")
                .accountType("AHORROS")
                .initialBalance(new BigDecimal("2000.00"))
                .status(true)
                .customerId("ABC12345")
                .build();
    }

    @Test
    @DisplayName("Debe crear un depósito exitosamente")
    void crear_DebeRegistrarDeposito_CuandoElValorEsPositivo() {
        MovementRequestDTO request = MovementRequestDTO.builder()
                .accountNumber("478758")
                .value(new BigDecimal("600.00"))
                .build();

        Movement savedMovement = Movement.builder()
                .id(1L)
                .date(LocalDateTime.now())
                .movementType("Deposito")
                .value(new BigDecimal("600.00"))
                .balance(new BigDecimal("2600.00"))
                .account(account)
                .build();

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(movementRepository.findTopByAccountAccountNumberOrderByDateDesc("478758"))
                .thenReturn(Optional.empty());
        when(movementRepository.save(any(Movement.class))).thenReturn(savedMovement);

        MovementResponseDTO result = movementService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getMovementType()).isEqualTo("Deposito");
        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("2600.00"));
        verify(movementRepository, times(1)).save(any(Movement.class));
    }

    @Test
    @DisplayName("Debe crear un retiro exitosamente")
    void crear_DebeRegistrarRetiro_CuandoHaySaldoSuficiente() {
        MovementRequestDTO request = MovementRequestDTO.builder()
                .accountNumber("478758")
                .value(new BigDecimal("-575.00"))
                .build();

        Movement savedMovement = Movement.builder()
                .id(2L)
                .date(LocalDateTime.now())
                .movementType("Retiro")
                .value(new BigDecimal("-575.00"))
                .balance(new BigDecimal("1425.00"))
                .account(account)
                .build();

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(movementRepository.findTopByAccountAccountNumberOrderByDateDesc("478758"))
                .thenReturn(Optional.empty());
        when(movementRepository.save(any(Movement.class))).thenReturn(savedMovement);

        MovementResponseDTO result = movementService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getMovementType()).isEqualTo("Retiro");
        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("1425.00"));
    }

    @Test
    @DisplayName("Debe lanzar excepción por saldo insuficiente")
    void crear_DebeLanzarExcepcion_CuandoSaldoInsuficiente() {
        MovementRequestDTO request = MovementRequestDTO.builder()
                .accountNumber("478758")
                .value(new BigDecimal("-3000.00"))
                .build();

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(movementRepository.findTopByAccountAccountNumberOrderByDateDesc("478758"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> movementService.create(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Saldo no disponible");

        verify(movementRepository, never()).save(any(Movement.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la cuenta no existe")
    void crear_DebeLanzarExcepcion_CuandoCuentaNoEncontrada() {
        MovementRequestDTO request = MovementRequestDTO.builder()
                .accountNumber("INVALID")
                .value(new BigDecimal("100.00"))
                .build();

        when(accountRepository.findByAccountNumber("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movementService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cuenta no encontrada con n");
    }

    @Test
    @DisplayName("Debe calcular saldo basado en el último movimiento")
    void crear_DebeUsarSaldoDelUltimoMovimiento_CuandoExistenMovimientos() {
        Movement lastMovement = Movement.builder()
                .id(5L)
                .balance(new BigDecimal("500.00"))
                .account(account)
                .build();

        MovementRequestDTO request = MovementRequestDTO.builder()
                .accountNumber("478758")
                .value(new BigDecimal("-600.00"))
                .build();

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(movementRepository.findTopByAccountAccountNumberOrderByDateDesc("478758"))
                .thenReturn(Optional.of(lastMovement));

        assertThatThrownBy(() -> movementService.create(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Saldo no disponible");
    }

    @Test
    @DisplayName("Debe manejar depósitos secuenciales correctamente")
    void crear_DebeManejarDepositosSecuenciales() {
        Movement lastMovement = Movement.builder()
                .id(3L)
                .balance(new BigDecimal("2600.00"))
                .account(account)
                .build();

        MovementRequestDTO request = MovementRequestDTO.builder()
                .accountNumber("478758")
                .value(new BigDecimal("100.00"))
                .build();

        Movement savedMovement = Movement.builder()
                .id(4L)
                .date(LocalDateTime.now())
                .movementType("Deposito")
                .value(new BigDecimal("100.00"))
                .balance(new BigDecimal("2700.00"))
                .account(account)
                .build();

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(movementRepository.findTopByAccountAccountNumberOrderByDateDesc("478758"))
                .thenReturn(Optional.of(lastMovement));
        when(movementRepository.save(any(Movement.class))).thenReturn(savedMovement);

        MovementResponseDTO result = movementService.create(request);

        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("2700.00"));
    }
}
