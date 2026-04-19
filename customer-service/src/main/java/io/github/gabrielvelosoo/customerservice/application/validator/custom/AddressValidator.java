package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.AuthService;
import io.github.gabrielvelosoo.customerservice.application.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressValidator {

    private final AuthService authService;

    public void validateOnUpdateAndDelete(Address address) {
        Customer loggedCustomer = authService.getLoggedCustomer();
        if(!addressBelongsToLoggedCustomer(address, loggedCustomer)) {
            throw new BusinessRuleException("You don’t have permission to manage this address");
        }
    }

    private boolean addressBelongsToLoggedCustomer(Address address, Customer loggedCustomer) {
        return loggedCustomer != null
                && address.getCustomer() != null
                && loggedCustomer.getId().equals(address.getCustomer().getId());
    }
}
