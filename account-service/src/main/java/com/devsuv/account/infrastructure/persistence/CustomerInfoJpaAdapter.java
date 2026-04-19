package com.devsuv.account.infrastructure.persistence;

import com.devsuv.account.domain.model.CustomerInfo;
import com.devsuv.account.domain.port.CustomerInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerInfoJpaAdapter implements CustomerInfoRepository {

    private final SpringDataCustomerInfoRepository springDataRepository;

    @Override
    public Optional<CustomerInfo> findByCustomerId(String customerId) {
        return springDataRepository.findByCustomerId(customerId);
    }

    @Override
    public CustomerInfo save(CustomerInfo customerInfo) {
        return springDataRepository.save(customerInfo);
    }
}
