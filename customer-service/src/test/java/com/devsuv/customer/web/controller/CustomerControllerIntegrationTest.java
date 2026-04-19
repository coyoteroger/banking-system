package com.devsuv.customer.web.controller;

import com.devsuv.customer.application.dto.CustomerRequestDTO;
import com.devsuv.customer.domain.model.Customer;
import com.devsuv.customer.infrastructure.messaging.CustomerEventPublisher;
import com.devsuv.customer.infrastructure.persistence.SpringDataCustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataCustomerRepository repository;

    @MockBean
    private CustomerEventPublisher customerEventPublisher;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Integración: Debe crear y recuperar un cliente")
    void debeCrearYRecuperarCliente() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .name("Marianela Montalvo")
                .gender("FEMENINO")
                .age(25)
                .identification("0987654321")
                .address("Amazonas y NNUU")
                .phone("097548965")
                .password("5678")
                .status(true)
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.name").value("Marianela Montalvo"))
                .andExpect(jsonPath("$.item.customerId").isNotEmpty())
                .andExpect(jsonPath("$.item.status").value(true))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item", hasSize(1)))
                .andExpect(jsonPath("$.item[0].name").value("Marianela Montalvo"));
    }

    @Test
    @DisplayName("Integración: Debe retornar 400 al crear cliente con datos inválidos")
    void debeRetornar400CuandoDatosInvalidos() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .name("")
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.item.errorCode").isNotEmpty());
    }

    @Test
    @DisplayName("Integración: Debe retornar 404 cuando el cliente no existe")
    void debeRetornar404CuandoClienteNoEncontrado() throws Exception {
        mockMvc.perform(get("/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Cliente no encontrado con id: 999"));
    }

    @Test
    @DisplayName("Integración: Debe actualizar cliente exitosamente")
    void debeActualizarClienteExitosamente() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId("TEST0001");
        customer.setName("Jose Lema");
        customer.setGender("MASCULINO");
        customer.setAge(30);
        customer.setIdentification("1234567890");
        customer.setAddress("Otavalo sn y principal");
        customer.setPhone("098254785");
        customer.setPassword("encoded1234");
        customer.setStatus(true);
        Customer saved = repository.save(customer);

        CustomerRequestDTO updateRequest = CustomerRequestDTO.builder()
                .name("Jose Lema Updated")
                .gender("MASCULINO")
                .age(31)
                .identification("1234567890")
                .address("Quito y 10 de Agosto")
                .phone("098254785")
                .password("1234")
                .status(true)
                .build();

        mockMvc.perform(put("/clientes/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name").value("Jose Lema Updated"))
                .andExpect(jsonPath("$.item.age").value(31));
    }

    @Test
    @DisplayName("Integración: Debe hacer soft-delete y retornar estado desactivado")
    void debeDesactivarClienteYRetornarEstado() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId("TEST0002");
        customer.setName("To Deactivate");
        customer.setGender("MASCULINO");
        customer.setAge(30);
        customer.setIdentification("9999999999");
        customer.setAddress("Test Address");
        customer.setPhone("000000000");
        customer.setPassword("encoded1234");
        customer.setStatus(true);
        Customer saved = repository.save(customer);

        mockMvc.perform(delete("/clientes/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.status").value(false))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        mockMvc.perform(get("/clientes/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.status").value(false));
    }
}
