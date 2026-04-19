package com.devsuv.customer.web.controller;

import com.devsuv.customer.application.dto.CustomerRequestDTO;
import com.devsuv.customer.application.dto.CustomerResponseDTO;
import com.devsuv.customer.application.service.CustomerService;
import com.devsuv.customer.common.constant.ApiStatus;
import com.devsuv.customer.common.response.GenericResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<GenericResponseDto<List<CustomerResponseDTO>>> findAll() {
        List<CustomerResponseDTO> data = customerService.findAll();
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Clientes obtenidos correctamente", ApiStatus.SUCCESS.name()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<CustomerResponseDTO>> findById(@PathVariable Long id) {
        CustomerResponseDTO data = customerService.findById(id);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cliente obtenido correctamente", ApiStatus.SUCCESS.name()));
    }

    @GetMapping("/buscar/{customerId}")
    public ResponseEntity<GenericResponseDto<CustomerResponseDTO>> findByCustomerId(@PathVariable String customerId) {
        CustomerResponseDTO data = customerService.findByCustomerId(customerId);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cliente obtenido correctamente", ApiStatus.SUCCESS.name()));
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<CustomerResponseDTO>> create(@Valid @RequestBody CustomerRequestDTO dto) {
        CustomerResponseDTO data = customerService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseDto<>(data, "Cliente creado correctamente", ApiStatus.SUCCESS.name()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponseDto<CustomerResponseDTO>> update(@PathVariable Long id,
                                                                           @Valid @RequestBody CustomerRequestDTO dto) {
        CustomerResponseDTO data = customerService.update(id, dto);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cliente actualizado correctamente", ApiStatus.SUCCESS.name()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GenericResponseDto<CustomerResponseDTO>> patch(@PathVariable Long id,
                                                                          @RequestBody CustomerRequestDTO dto) {
        CustomerResponseDTO data = customerService.patch(id, dto);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cliente actualizado correctamente", ApiStatus.SUCCESS.name()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponseDto<CustomerResponseDTO>> delete(@PathVariable Long id) {
        CustomerResponseDTO data = customerService.delete(id);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cliente desactivado correctamente", ApiStatus.SUCCESS.name()));
    }
}
