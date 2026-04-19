package com.devsuv.customer.application.mapper;

import com.devsuv.customer.application.dto.CustomerRequestDTO;
import com.devsuv.customer.application.dto.CustomerResponseDTO;
import com.devsuv.customer.domain.model.Customer;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static Customer toEntity(CustomerRequestDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setGender(dto.getGender());
        customer.setAge(dto.getAge());
        customer.setIdentification(dto.getIdentification());
        customer.setAddress(dto.getAddress());
        customer.setPhone(dto.getPhone());
        customer.setPassword(dto.getPassword());
        customer.setStatus(true);
        return customer;
    }

    public static CustomerResponseDTO toDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .gender(customer.getGender())
                .age(customer.getAge())
                .identification(customer.getIdentification())
                .address(customer.getAddress())
                .phone(customer.getPhone())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    public static void updateEntity(Customer customer, CustomerRequestDTO dto) {
        customer.setName(dto.getName());
        customer.setGender(dto.getGender());
        customer.setAge(dto.getAge());
        customer.setIdentification(dto.getIdentification());
        customer.setAddress(dto.getAddress());
        customer.setPhone(dto.getPhone());
        customer.setPassword(dto.getPassword());
        if (dto.getStatus() != null) customer.setStatus(dto.getStatus());
    }

    public static void patchEntity(Customer customer, CustomerRequestDTO dto) {
        if (dto.getName() != null) customer.setName(dto.getName());
        if (dto.getGender() != null) customer.setGender(dto.getGender());
        if (dto.getAge() != null) customer.setAge(dto.getAge());
        if (dto.getIdentification() != null) customer.setIdentification(dto.getIdentification());
        if (dto.getAddress() != null) customer.setAddress(dto.getAddress());
        if (dto.getPhone() != null) customer.setPhone(dto.getPhone());
        if (dto.getStatus() != null) customer.setStatus(dto.getStatus());
    }
}
