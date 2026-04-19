package com.devsuv.account.infrastructure.persistence;

import com.devsuv.account.domain.model.Movement;
import com.devsuv.account.domain.port.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MovementJpaAdapter implements MovementRepository {

    private final SpringDataMovementRepository springDataRepository;

    @Override
    public List<Movement> findAll() {
        return springDataRepository.findAll();
    }

    @Override
    public Optional<Movement> findById(Long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public List<Movement> findByAccountAccountNumber(String accountNumber) {
        return springDataRepository.findByAccountAccountNumber(accountNumber);
    }

    @Override
    public List<Movement> findByAccountAccountNumberAndDateBetween(String accountNumber,
                                                                    LocalDateTime startDate,
                                                                    LocalDateTime endDate) {
        return springDataRepository.findByAccountAccountNumberAndDateBetween(accountNumber, startDate, endDate);
    }

    @Override
    public Optional<Movement> findTopByAccountAccountNumberOrderByDateDesc(String accountNumber) {
        return springDataRepository.findTopByAccountAccountNumberOrderByDateDesc(accountNumber);
    }

    @Override
    public Movement save(Movement movement) {
        return springDataRepository.save(movement);
    }
}
