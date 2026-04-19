package io.github.gabrielvelosoo.customerservice.infrastructure.service;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.service.AddressService;
import io.github.gabrielvelosoo.customerservice.infrastructure.persistence.repository.AddressRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.persistence.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.application.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public Address findById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow( () -> new RecordNotFoundException("Address not found: " + addressId) );
    }

    @Override
    public List<Address> getAddressesLoggedCustomer(Long customerId) {
        if(!customerRepository.existsById(customerId)) {
            throw new RecordNotFoundException("Customer not found: " + customerId);
        }
        return addressRepository.findByCustomerId(customerId);
    }

    @Override
    public void delete(Address address) {
        addressRepository.delete(address);
    }
}
