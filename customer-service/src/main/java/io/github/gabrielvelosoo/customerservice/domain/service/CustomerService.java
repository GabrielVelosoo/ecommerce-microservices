package io.github.gabrielvelosoo.customerservice.domain.service;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomerService {

    Customer save(Customer customer);
    Customer findById(Long id);
    Customer findByKeycloakUserId(String keycloakUserId);
    Page<Customer> findCustomers(Specification<Customer> spec, Pageable pageable);
    void delete(Customer customer);
}
