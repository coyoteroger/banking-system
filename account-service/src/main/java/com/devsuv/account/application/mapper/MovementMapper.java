package com.devsuv.account.application.mapper;

import com.devsuv.account.application.dto.MovementResponseDTO;
import com.devsuv.account.domain.model.Movement;

public final class MovementMapper {

    private MovementMapper() {
    }

    public static MovementResponseDTO toDTO(Movement movement) {
        return MovementResponseDTO.builder()
                .id(movement.getId())
                .date(movement.getDate())
                .movementType(movement.getMovementType())
                .value(movement.getValue())
                .balance(movement.getBalance())
                .accountNumber(movement.getAccount().getAccountNumber())
                .build();
    }
}
