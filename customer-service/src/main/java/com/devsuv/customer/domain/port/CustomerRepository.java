package com.devsuv.customer.domain.port;

import com.devsuv.customer.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    List<Customer> findAll();

    Optional<Customer> findById(Long id);

    Optional<Customer> findByCustomerId(String customerId);

    Optional<Customer> findByIdentification(String identification);

    Customer save(Customer customer);

    void deleteById(Long id);

    boolean existsById(Long id);
}
