package com.devsuv.customer.infrastructure.persistence;

import com.devsuv.customer.domain.model.Customer;
import com.devsuv.customer.domain.port.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerJpaAdapter implements CustomerRepository {

    private final SpringDataCustomerRepository springDataRepository;

    @Override
    public List<Customer> findAll() {
        return springDataRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public Optional<Customer> findByCustomerId(String customerId) {
        return springDataRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<Customer> findByIdentification(String identification) {
        return springDataRepository.findByIdentification(identification);
    }

    @Override
    public Customer save(Customer customer) {
        return springDataRepository.save(customer);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return springDataRepository.existsById(id);
    }
}
