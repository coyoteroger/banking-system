package com.devsuv.account.infrastructure.persistence;

import com.devsuv.account.domain.port.CustomerExistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerExistenceJpaAdapter implements CustomerExistenceRepository {

    private final SpringDataCustomerInfoRepository springDataRepository;

    @Override
    public boolean existsByCustomerIdAndStatusTrue(String customerId) {
        return springDataRepository.existsByCustomerIdAndStatusTrue(customerId);
    }
}
