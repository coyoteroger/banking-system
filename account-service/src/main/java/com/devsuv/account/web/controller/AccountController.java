package com.devsuv.account.web.controller;

import com.devsuv.account.application.dto.AccountRequestDTO;
import com.devsuv.account.application.dto.AccountResponseDTO;
import com.devsuv.account.application.service.AccountService;
import com.devsuv.account.common.constant.ApiStatus;
import com.devsuv.account.common.response.GenericResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<GenericResponseDto<List<AccountResponseDTO>>> findAll() {
        List<AccountResponseDTO> data = accountService.findAll();
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cuentas obtenidas correctamente", ApiStatus.SUCCESS.name()));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<GenericResponseDto<AccountResponseDTO>> findByAccountNumber(@PathVariable String accountNumber) {
        AccountResponseDTO data = accountService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cuenta obtenida correctamente", ApiStatus.SUCCESS.name()));
    }

    @GetMapping("/cliente/{customerId}")
    public ResponseEntity<GenericResponseDto<List<AccountResponseDTO>>> findByCustomerId(@PathVariable String customerId) {
        List<AccountResponseDTO> data = accountService.findByCustomerId(customerId);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cuentas obtenidas correctamente", ApiStatus.SUCCESS.name()));
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<AccountResponseDTO>> create(@Valid @RequestBody AccountRequestDTO dto) {
        AccountResponseDTO data = accountService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseDto<>(data, "Cuenta creada correctamente", ApiStatus.SUCCESS.name()));
    }

    @PutMapping("/{accountNumber}")
    public ResponseEntity<GenericResponseDto<AccountResponseDTO>> update(@PathVariable String accountNumber,
                                                                          @Valid @RequestBody AccountRequestDTO dto) {
        AccountResponseDTO data = accountService.update(accountNumber, dto);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cuenta actualizada correctamente", ApiStatus.SUCCESS.name()));
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<GenericResponseDto<AccountResponseDTO>> patch(@PathVariable String accountNumber,
                                                                         @RequestBody AccountRequestDTO dto) {
        AccountResponseDTO data = accountService.patch(accountNumber, dto);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cuenta actualizada correctamente", ApiStatus.SUCCESS.name()));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<GenericResponseDto<AccountResponseDTO>> delete(@PathVariable String accountNumber) {
        AccountResponseDTO data = accountService.delete(accountNumber);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Cuenta desactivada correctamente", ApiStatus.SUCCESS.name()));
    }
}
