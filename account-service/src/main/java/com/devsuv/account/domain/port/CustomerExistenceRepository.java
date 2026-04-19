package com.devsuv.account.domain.port;

public interface CustomerExistenceRepository {
    boolean existsByCustomerIdAndStatusTrue(String customerId);
}
