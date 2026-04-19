package io.github.gabrielvelosoo.customerservice.domain.service;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;

public interface AuthService {

    Customer getLoggedCustomer();
}
