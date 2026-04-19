package com.devsuv.customer.application.service;

import com.devsuv.customer.application.dto.CustomerEventDTO;
import com.devsuv.customer.application.dto.CustomerRequestDTO;
import com.devsuv.customer.application.dto.CustomerResponseDTO;
import com.devsuv.customer.domain.exception.BusinessException;
import com.devsuv.customer.domain.exception.ResourceNotFoundException;
import com.devsuv.customer.domain.model.Customer;
import com.devsuv.customer.domain.port.CustomerRepository;
import com.devsuv.customer.infrastructure.messaging.CustomerEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomerEventPublisher customerEventPublisher;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCustomerId("ABC12345");
        customer.setName("Jose Lema");
        customer.setGender("MASCULINO");
        customer.setAge(30);
        customer.setIdentification("1234567890");
        customer.setAddress("Otavalo sn y principal");
        customer.setPhone("098254785");
        customer.setPassword("encodedPassword");
        customer.setStatus(true);

        requestDTO = CustomerRequestDTO.builder()
                .name("Jose Lema")
                .gender("MASCULINO")
                .age(30)
                .identification("1234567890")
                .address("Otavalo sn y principal")
                .phone("098254785")
                .password("1234")
                .status(true)
                .build();
    }

    @Test
    @DisplayName("Debe retornar todos los clientes")
    void findAll_DebeRetornarTodosLosClientes() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));

        List<CustomerResponseDTO> result = customerService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jose Lema");
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar cliente por id")
    void findById_DebeRetornarCliente_CuandoExiste() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponseDTO result = customerService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Jose Lema");
        assertThat(result.getCustomerId()).isEqualTo("ABC12345");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cliente no existe por id")
    void findById_DebeLanzarExcepcion_CuandoNoExiste() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado con id: 99");
    }

    @Test
    @DisplayName("Debe crear cliente exitosamente con contraseña codificada")
    void crear_DebeCrearCliente_CuandoIdentificacionEsUnica() {
        when(customerRepository.findByIdentification(requestDTO.getIdentification()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        doNothing().when(customerEventPublisher).publish(any(CustomerEventDTO.class));

        CustomerResponseDTO result = customerService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Jose Lema");
        verify(passwordEncoder, times(1)).encode("1234");
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(customerEventPublisher, times(1)).publish(any(CustomerEventDTO.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear cliente con identificación duplicada")
    void crear_DebeLanzarExcepcion_CuandoIdentificacionExiste() {
        when(customerRepository.findByIdentification(requestDTO.getIdentification()))
                .thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> customerService.create(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe un cliente con identificación");
    }

    @Test
    @DisplayName("Debe actualizar cliente exitosamente")
    void actualizar_DebeActualizarCliente_CuandoExiste() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.findByIdentification(requestDTO.getIdentification()))
                .thenReturn(Optional.of(customer));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        doNothing().when(customerEventPublisher).publish(any(CustomerEventDTO.class));

        CustomerResponseDTO result = customerService.update(1L, requestDTO);

        assertThat(result).isNotNull();
        verify(passwordEncoder, times(1)).encode("1234");
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(customerEventPublisher, times(1)).publish(any(CustomerEventDTO.class));
    }

    @Test
    @DisplayName("Debe hacer soft-delete y publicar evento DELETED")
    void eliminar_DebeDesactivarCliente_CuandoExiste() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        doNothing().when(customerEventPublisher).publish(any(CustomerEventDTO.class));

        CustomerResponseDTO result = customerService.delete(1L);

        assertThat(customer.getStatus()).isFalse();
        assertThat(result).isNotNull();
        verify(customerRepository, times(1)).save(customer);
        verify(customerEventPublisher, times(1)).publish(any(CustomerEventDTO.class));
        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar cliente inexistente")
    void eliminar_DebeLanzarExcepcion_CuandoNoExiste() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado con id: 99");
    }
}
