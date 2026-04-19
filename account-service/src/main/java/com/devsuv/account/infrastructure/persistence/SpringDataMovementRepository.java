package com.devsuv.account.infrastructure.persistence;

import com.devsuv.account.domain.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataMovementRepository extends JpaRepository<Movement, Long> {

    List<Movement> findByAccountAccountNumber(String accountNumber);

    List<Movement> findByAccountAccountNumberAndDateBetween(String accountNumber,
                                                             LocalDateTime startDate,
                                                             LocalDateTime endDate);

    Optional<Movement> findTopByAccountAccountNumberOrderByDateDesc(String accountNumber);
}
