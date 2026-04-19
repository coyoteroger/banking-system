package com.devsuv.account.infrastructure.persistence;

import com.devsuv.account.domain.model.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataCustomerInfoRepository extends JpaRepository<CustomerInfo, Long> {
    boolean existsByCustomerIdAndStatusTrue(String customerId);
    Optional<CustomerInfo> findByCustomerId(String customerId);
}
