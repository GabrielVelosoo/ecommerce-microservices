package io.github.gabrielvelosoo.customerservice.unit.infrastructure.service;

import io.github.gabrielvelosoo.customerservice.application.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.infrastructure.persistence.repository.AddressRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.persistence.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.service.AddressServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    AddressRepository addressRepository;

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    AddressServiceImpl addressService;

    @Nested
    class FindByIdTests {

        @Test
        void shouldReturnAddressWhenExists() {
            Address address = new Address();
            when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
            Address result = addressService.findById(1L);
            assertEquals(address, result);
            verify(addressRepository).findById(1L);
        }

        @Test
        void shouldThrowExceptionWhenAddressNotFound() {
            when(addressRepository.findById(1L)).thenReturn(Optional.empty());
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> addressService.findById(1L)
            );
            assertEquals("Address not found: 1", e.getMessage());
        }
    }

    @Nested
    class GetAddressesLoggedCustomerTests {

        @Test
        void shouldThrowExceptionWhenCustomerDoesNotExist() {
            when(customerRepository.existsById(1L)).thenReturn(false);
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> addressService.getAddressesLoggedCustomer(1L)
            );
            assertEquals("Customer not found: 1", e.getMessage());
        }

        @Test
        void shouldReturnAddressesWhenCustomerExists() {
            List<Address> addresses = List.of(new Address(), new Address());
            when(customerRepository.existsById(1L)).thenReturn(true);
            when(addressRepository.findByCustomerId(1L)).thenReturn(addresses);
            List<Address> result = addressService.getAddressesLoggedCustomer(1L);
            assertEquals(2, result.size());
        }
    }
}