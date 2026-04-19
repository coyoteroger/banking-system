package com.devsuv.customer.application.service;

import com.devsuv.customer.application.dto.CustomerEventDTO;
import com.devsuv.customer.application.dto.CustomerRequestDTO;
import com.devsuv.customer.application.dto.CustomerResponseDTO;
import com.devsuv.customer.application.mapper.CustomerMapper;
import com.devsuv.customer.domain.exception.BusinessException;
import com.devsuv.customer.domain.exception.ResourceNotFoundException;
import com.devsuv.customer.domain.model.Customer;
import com.devsuv.customer.domain.port.CustomerRepository;
import com.devsuv.customer.infrastructure.messaging.CustomerEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerEventPublisher customerEventPublisher;

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> findAll() {
        log.info("Fetching all customers");
        return customerRepository.findAll().stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO findById(Long id) {
        log.info("Fetching customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return CustomerMapper.toDTO(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO findByCustomerId(String customerId) {
        log.info("Fetching customer with customerId: {}", customerId);
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con customerId: " + customerId));
        return CustomerMapper.toDTO(customer);
    }

    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        log.info("Creating customer with identification: {}", dto.getIdentification());

        customerRepository.findByIdentification(dto.getIdentification())
                .ifPresent(c -> {
                    throw new BusinessException("Ya existe un cliente con identificación: " + dto.getIdentification());
                });

        Customer customer = CustomerMapper.toEntity(dto);
        customer.setCustomerId(generateCustomerId());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));

        Customer saved = customerRepository.save(customer);
        log.info("Customer created successfully with id: {} and customerId: {}", saved.getId(), saved.getCustomerId());

        customerEventPublisher.publish(CustomerEventDTO.builder()
                .customerId(saved.getCustomerId())
                .name(saved.getName())
                .status(saved.getStatus())
                .operation("CREATED")
                .build());

        return CustomerMapper.toDTO(saved);
    }

    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        log.info("Updating customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        customerRepository.findByIdentification(dto.getIdentification())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(c -> {
                    throw new BusinessException("Ya existe otro cliente con identificación: " + dto.getIdentification());
                });

        CustomerMapper.updateEntity(customer, dto);
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        Customer saved = customerRepository.save(customer);

        log.info("Customer updated successfully with id: {}", saved.getId());

        customerEventPublisher.publish(CustomerEventDTO.builder()
                .customerId(saved.getCustomerId())
                .name(saved.getName())
                .status(saved.getStatus())
                .operation("UPDATED")
                .build());

        return CustomerMapper.toDTO(saved);
    }

    public CustomerResponseDTO patch(Long id, CustomerRequestDTO dto) {
        log.info("Patching customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        if (dto.getIdentification() != null) {
            customerRepository.findByIdentification(dto.getIdentification())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(c -> {
                        throw new BusinessException("Ya existe otro cliente con identificación: " + dto.getIdentification());
                    });
        }

        CustomerMapper.patchEntity(customer, dto);
        if (dto.getPassword() != null) {
            customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        Customer saved = customerRepository.save(customer);

        log.info("Customer patched successfully with id: {}", saved.getId());

        customerEventPublisher.publish(CustomerEventDTO.builder()
                .customerId(saved.getCustomerId())
                .name(saved.getName())
                .status(saved.getStatus())
                .operation("UPDATED")
                .build());

        return CustomerMapper.toDTO(saved);
    }

    public CustomerResponseDTO delete(Long id) {
        log.info("Deactivating customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        customer.setStatus(false);
        Customer saved = customerRepository.save(customer);
        log.info("Customer deactivated (soft delete) successfully with id: {}", saved.getId());

        customerEventPublisher.publish(CustomerEventDTO.builder()
                .customerId(saved.getCustomerId())
                .name(saved.getName())
                .status(saved.getStatus())
                .operation("DELETED")
                .build());

        return CustomerMapper.toDTO(saved);
    }

    private String generateCustomerId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}