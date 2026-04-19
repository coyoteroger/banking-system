package com.devsuv.account.domain.port;

import com.devsuv.account.domain.model.CustomerInfo;

import java.util.Optional;

public interface CustomerInfoRepository {
    Optional<CustomerInfo> findByCustomerId(String customerId);
    CustomerInfo save(CustomerInfo customerInfo);
}
