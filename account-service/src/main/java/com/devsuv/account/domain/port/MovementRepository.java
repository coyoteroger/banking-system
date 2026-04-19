package com.devsuv.account.domain.port;

import com.devsuv.account.domain.model.Movement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovementRepository {

    List<Movement> findAll();

    Optional<Movement> findById(Long id);

    List<Movement> findByAccountAccountNumber(String accountNumber);

    List<Movement> findByAccountAccountNumberAndDateBetween(String accountNumber,
                                                             LocalDateTime startDate,
                                                             LocalDateTime endDate);

    Optional<Movement> findTopByAccountAccountNumberOrderByDateDesc(String accountNumber);

    Movement save(Movement movement);
}
