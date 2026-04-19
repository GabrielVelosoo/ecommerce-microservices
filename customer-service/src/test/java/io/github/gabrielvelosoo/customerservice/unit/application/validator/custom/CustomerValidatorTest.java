package io.github.gabrielvelosoo.customerservice.unit.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.CustomerValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.infrastructure.persistence.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.application.exception.DuplicateRecordException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerValidatorTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerValidator customerValidator;

    @Nested
    class ValidateOnCreateTests {

        @Test
        void shouldThrowExceptionWhenEmailAlreadyExistsOnCreate() {
            Customer customer = new Customer();
            customer.setEmail("test@email.com");
            customer.setCpf("12345678900");
            when(customerRepository.findByEmail("test@email.com"))
                    .thenReturn(Optional.of(new Customer()));
            DuplicateRecordException exception = assertThrows(
                    DuplicateRecordException.class,
                    () -> customerValidator.validateOnCreate(customer)
            );
            assertEquals(
                    "There is already an account registered with this e-mail",
                    exception.getMessage()
            );
            verify(customerRepository).findByEmail("test@email.com");
            verify(customerRepository, never()).findByCpf(any());
        }

        @Test
        void shouldThrowExceptionWhenCpfAlreadyExistsOnCreate() {
            Customer customer = new Customer();
            customer.setEmail("test@email.com");
            customer.setCpf("12345678900");
            when(customerRepository.findByEmail("test@email.com"))
                    .thenReturn(Optional.empty());
            when(customerRepository.findByCpf("12345678900"))
                    .thenReturn(Optional.of(new Customer()));
            DuplicateRecordException exception = assertThrows(
                    DuplicateRecordException.class,
                    () -> customerValidator.validateOnCreate(customer)
            );
            assertEquals(
                    "There is already an account registered with this CPF",
                    exception.getMessage()
            );
            verify(customerRepository).findByEmail("test@email.com");
            verify(customerRepository).findByCpf("12345678900");
        }

        @Test
        void shouldPassValidationOnCreateWhenEmailAndCpfAreUnique() {
            Customer customer = new Customer();
            customer.setEmail("test@email.com");
            customer.setCpf("12345678900");
            when(customerRepository.findByEmail("test@email.com"))
                    .thenReturn(Optional.empty());
            when(customerRepository.findByCpf("12345678900"))
                    .thenReturn(Optional.empty());
            assertDoesNotThrow(() -> customerValidator.validateOnCreate(customer));
            verify(customerRepository).findByEmail("test@email.com");
            verify(customerRepository).findByCpf("12345678900");
        }


    }

    @Nested
    class ValidateOnUpdateTests {

        @Test
        void shouldThrowExceptionWhenCpfAlreadyExistsOnUpdate() {
            CustomerUpdateDTO updateDTO = new CustomerUpdateDTO(
                    "NovoNome",
                    "NovoSobrenome",
                    "12345678900",
                    null
            );
            when(customerRepository.findByCpfAndNotId("12345678900", 1L))
                    .thenReturn(Optional.of(new Customer()));
            DuplicateRecordException exception = assertThrows(
                    DuplicateRecordException.class,
                    () -> customerValidator.validateOnUpdate(1L, updateDTO)
            );
            assertEquals(
                    "There is already an account registered with this CPF",
                    exception.getMessage()
            );
            verify(customerRepository)
                    .findByCpfAndNotId("12345678900", 1L);
        }

        @Test
        void shouldPassValidationOnUpdateWhenCpfIsUnique() {
            CustomerUpdateDTO updateDTO = new CustomerUpdateDTO(
                    "NovoNome",
                    "NovoSobrenome",
                    "12345678900",
                    null
            );
            when(customerRepository.findByCpfAndNotId("12345678900", 1L))
                    .thenReturn(Optional.empty());
            assertDoesNotThrow(
                    () -> customerValidator.validateOnUpdate(1L, updateDTO)
            );
            verify(customerRepository)
                    .findByCpfAndNotId("12345678900", 1L);
        }
    }
}