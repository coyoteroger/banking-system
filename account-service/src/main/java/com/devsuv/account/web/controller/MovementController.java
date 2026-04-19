package com.devsuv.account.web.controller;

import com.devsuv.account.application.dto.MovementRequestDTO;
import com.devsuv.account.application.dto.MovementResponseDTO;
import com.devsuv.account.application.service.MovementService;
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
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Slf4j
public class MovementController {

    private final MovementService movementService;

    @GetMapping
    public ResponseEntity<GenericResponseDto<List<MovementResponseDTO>>> findAll() {
        List<MovementResponseDTO> data = movementService.findAll();
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Movimientos obtenidos correctamente", ApiStatus.SUCCESS.name()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<MovementResponseDTO>> findById(@PathVariable Long id) {
        MovementResponseDTO data = movementService.findById(id);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Movimiento obtenido correctamente", ApiStatus.SUCCESS.name()));
    }

    @GetMapping("/cuenta/{accountNumber}")
    public ResponseEntity<GenericResponseDto<List<MovementResponseDTO>>> findByAccountNumber(@PathVariable String accountNumber) {
        List<MovementResponseDTO> data = movementService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(new GenericResponseDto<>(data, "Movimientos obtenidos correctamente", ApiStatus.SUCCESS.name()));
    }

    @PostMapping
    public ResponseEntity<GenericResponseDto<MovementResponseDTO>> create(@Valid @RequestBody MovementRequestDTO dto) {
        MovementResponseDTO data = movementService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseDto<>(data, "Movimiento creado correctamente", ApiStatus.SUCCESS.name()));
    }
}
