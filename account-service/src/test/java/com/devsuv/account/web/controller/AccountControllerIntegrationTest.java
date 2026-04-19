package com.devsuv.account.web.controller;

import com.devsuv.account.application.dto.AccountRequestDTO;
import com.devsuv.account.application.dto.MovementRequestDTO;
import com.devsuv.account.domain.model.Account;
import com.devsuv.account.domain.model.CustomerInfo;
import com.devsuv.account.infrastructure.persistence.SpringDataAccountRepository;
import com.devsuv.account.infrastructure.persistence.SpringDataCustomerInfoRepository;
import com.devsuv.account.infrastructure.persistence.SpringDataMovementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataAccountRepository accountRepository;

    @Autowired
    private SpringDataMovementRepository movementRepository;

    @Autowired
    private SpringDataCustomerInfoRepository customerInfoRepository;

    @BeforeEach
    void setUp() {
        movementRepository.deleteAll();
        accountRepository.deleteAll();
        customerInfoRepository.deleteAll();

        CustomerInfo customer = CustomerInfo.builder()
                .customerId("ABC12345")
                .name("Jose Lema")
                .status(true)
                .build();
        customerInfoRepository.save(customer);

        CustomerInfo customer2 = CustomerInfo.builder()
                .customerId("DEF67890")
                .name("Marianela Montalvo")
                .status(true)
                .build();
        customerInfoRepository.save(customer2);
    }

    @Test
    @DisplayName("Integración: Debe crear cuenta y registrar movimientos")
    void debeCrearCuentaYRegistrarMovimientos() throws Exception {
        AccountRequestDTO accountRequest = AccountRequestDTO.builder()
                .accountNumber("478758")
                .accountType("AHORROS")
                .initialBalance(new BigDecimal("2000.00"))
                .status(true)
                .customerId("ABC12345")
                .build();

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.accountNumber").value("478758"))
                .andExpect(jsonPath("$.item.accountType").value("AHORROS"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        MovementRequestDTO depositRequest = MovementRequestDTO.builder()
                .accountNumber("478758")
                .value(new BigDecimal("600.00"))
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.movementType").value("Deposito"))
                .andExpect(jsonPath("$.item.balance").value(2600.00));

        MovementRequestDTO withdrawalRequest = MovementRequestDTO.builder()
                .accountNumber("478758")
                .value(new BigDecimal("-540.00"))
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.movementType").value("Retiro"))
                .andExpect(jsonPath("$.item.balance").value(2060.00));
    }

    @Test
    @DisplayName("Integración: Debe rechazar retiro cuando el saldo es insuficiente")
    void debeRechazarRetiroCuandoSaldoInsuficiente() throws Exception {
        Account account = Account.builder()
                .accountNumber("585545")
                .accountType("CORRIENTE")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .customerId("DEF67890")
                .build();
        accountRepository.save(account);

        MovementRequestDTO request = MovementRequestDTO.builder()
                .accountNumber("585545")
                .value(new BigDecimal("-150.00"))
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }

    @Test
    @DisplayName("Integración: Debe retornar todas las cuentas")
    void debeRetornarTodasLasCuentas() throws Exception {
        Account account = Account.builder()
                .accountNumber("495878")
                .accountType("AHORROS")
                .initialBalance(new BigDecimal("1000.00"))
                .status(true)
                .customerId("ABC12345")
                .build();
        accountRepository.save(account);

        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item", hasSize(1)))
                .andExpect(jsonPath("$.item[0].accountNumber").value("495878"));
    }

    @Test
    @DisplayName("Integración: Debe retornar 404 para cuenta inexistente")
    void debeRetornar404ParaCuentaInexistente() throws Exception {
        mockMvc.perform(get("/cuentas/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("Integración: Debe rechazar creación de cuenta si el cliente no existe")
    void debeRechazarCreacionCuentaSiClienteNoExiste() throws Exception {
        AccountRequestDTO accountRequest = AccountRequestDTO.builder()
                .accountNumber("999999")
                .accountType("AHORROS")
                .initialBalance(new BigDecimal("500.00"))
                .status(true)
                .customerId("NONEXISTENT")
                .build();

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("Integración: Debe rechazar actualización de cuenta con customerId inexistente")
    void debeRechazarActualizacionCuentaConClienteInexistente() throws Exception {
        Account account = Account.builder()
                .accountNumber("111111")
                .accountType("AHORROS")
                .initialBalance(new BigDecimal("500.00"))
                .status(true)
                .customerId("ABC12345")
                .build();
        accountRepository.save(account);

        AccountRequestDTO updateRequest = AccountRequestDTO.builder()
                .accountNumber("111111")
                .accountType("CORRIENTE")
                .initialBalance(new BigDecimal("1000.00"))
                .status(true)
                .customerId("NONEXISTENT")
                .build();

        mockMvc.perform(put("/cuentas/111111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("No se encontró un cliente activo con customerId: NONEXISTENT"));
    }

    @Test
    @DisplayName("Integración: Debe desactivar cuenta (soft delete)")
    void debeDesactivarCuenta() throws Exception {
        Account account = Account.builder()
                .accountNumber("333333")
                .accountType("AHORROS")
                .initialBalance(new BigDecimal("500.00"))
                .status(true)
                .customerId("ABC12345")
                .build();
        accountRepository.save(account);

        mockMvc.perform(delete("/cuentas/333333"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.status").value(false))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("Integración: Debe rechazar movimiento en cuenta inactiva")
    void debeRechazarMovimientoEnCuentaInactiva() throws Exception {
        Account account = Account.builder()
                .accountNumber("222222")
                .accountType("AHORROS")
                .initialBalance(new BigDecimal("500.00"))
                .status(false)
                .customerId("ABC12345")
                .build();
        accountRepository.save(account);

        MovementRequestDTO request = MovementRequestDTO.builder()
                .accountNumber("222222")
                .value(new BigDecimal("100.00"))
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("La cuenta 222222 se encuentra inactiva"));
    }
}

