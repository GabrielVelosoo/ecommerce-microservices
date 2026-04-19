package io.github.gabrielvelosoo.customerservice.infrastructure.service;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.CustomerService;
import io.github.gabrielvelosoo.customerservice.infrastructure.persistence.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.application.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer findById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow( () -> new RecordNotFoundException("Customer not found: " + customerId));
    }

    @Override
    public Customer findByKeycloakUserId(String keycloakUserId) {
        return customerRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow( () -> new RecordNotFoundException("Customer with this keycloakUserId not found: " + keycloakUserId));
    }

    @Override
    public Page<Customer> findCustomers(Specification<Customer> spec, Pageable pageable) {
        return customerRepository.findAll(spec, pageable);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }
}
