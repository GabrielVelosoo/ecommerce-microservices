package io.github.gabrielvelosoo.customerservice.unit.application.usecase.customer;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.application.mapper.CustomerMapper;
import io.github.gabrielvelosoo.customerservice.application.usecase.customer.CustomerUseCaseImpl;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.CustomerValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.CustomerService;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.producer.CustomerProducer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerUseCaseImplTest {

    @Mock
    CustomerService customerService;

    @Mock
    CustomerMapper customerMapper;

    @Mock
    CustomerValidator customerValidator;

    @Mock
    CustomerProducer customerProducer;

    @InjectMocks
    CustomerUseCaseImpl customerUseCase;

    @Nested
    class CreateTests {

        @Test
        void shouldCreateCustomerAndPublishEventSuccessfully() {
            CustomerRequestDTO requestDTO = new CustomerRequestDTO(
                    "Gabriel",
                    "Veloso",
                    "gabriel@email.com",
                    "123456",
                    "12345678900",
                    "01000-000",
                    LocalDate.of(2004, 4, 10)
            );
            Customer customer = new Customer();
            customer.setId(1L);
            customer.setName("Gabriel");
            customer.setLastName("Veloso");
            customer.setEmail("gabriel@email.com");
            CustomerResponseDTO responseDTO = new CustomerResponseDTO(
                    1L,
                    "Gabriel",
                    "Veloso",
                    "gabriel@email.com",
                    "12345678900",
                    "01000-000",
                    LocalDate.of(2004, 4, 10)
            );
            when(customerMapper.toEntity(requestDTO)).thenReturn(customer);
            when(customerService.save(customer)).thenReturn(customer);
            when(customerMapper.toDTO(customer)).thenReturn(responseDTO);
            CustomerResponseDTO result = customerUseCase.create(requestDTO);
            verify(customerValidator).validateOnCreate(customer);
            verify(customerService).save(customer);
            ArgumentCaptor<CustomerCreatedEvent> eventCaptor = ArgumentCaptor.forClass(CustomerCreatedEvent.class);
            verify(customerProducer).publishCustomerCreated(eventCaptor.capture());
            CustomerCreatedEvent event = eventCaptor.getValue();
            assertEquals(1L, event.customerId());
            assertEquals("Gabriel", event.name());
            assertEquals("Veloso", event.lastName());
            assertEquals("gabriel@email.com", event.email());
            assertEquals("123456", event.password());
            assertEquals(responseDTO, result);
        }
    }

    @Nested
    class EditTests {

        @Test
        void shouldEditCustomerAndPublishEventSuccessfully() {
            CustomerUpdateDTO updateDTO = new CustomerUpdateDTO(
                    "NovoNome",
                    "NovoSobrenome",
                    "12345678900",
                    LocalDate.of(2004, 4, 10)
            );
            Customer customer = new Customer();
            customer.setId(1L);
            customer.setName("Antigo");
            customer.setLastName("Nome");
            CustomerResponseDTO responseDTO = new CustomerResponseDTO(
                    1L,
                    "NovoNome",
                    "NovoSobrenome",
                    "email@email.com",
                    "12345678900",
                    "01000-000",
                    LocalDate.of(2000, 1, 1)
            );
            when(customerService.findById(1L)).thenReturn(customer);
            doAnswer(invocation -> {
                Customer c = invocation.getArgument(0);
                CustomerUpdateDTO dto = invocation.getArgument(1);
                c.setName(dto.name());
                c.setLastName(dto.lastName());
                c.setCpf(dto.cpf());
                c.setBirthDate(dto.birthDate());
                return null;
            }).when(customerMapper).edit(any(Customer.class), any(CustomerUpdateDTO.class));
            when(customerService.save(customer)).thenReturn(customer);
            when(customerMapper.toDTO(customer)).thenReturn(responseDTO);
            CustomerResponseDTO result = customerUseCase.edit(1L, updateDTO);
            verify(customerValidator).validateOnUpdate(1L, updateDTO);
            verify(customerMapper).edit(customer, updateDTO);
            verify(customerService).save(customer);
            ArgumentCaptor<CustomerUpdatedEvent> captor = ArgumentCaptor.forClass(CustomerUpdatedEvent.class);
            verify(customerProducer).publishCustomerUpdated(captor.capture());
            CustomerUpdatedEvent event = captor.getValue();
            assertEquals(1L, event.customerId());
            assertEquals("NovoNome", event.name());
            assertEquals("NovoSobrenome", event.lastName());
            assertEquals(responseDTO, result);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteCustomerAndPublishEventSuccessfully() {
            Customer customer = new Customer();
            customer.setId(1L);
            when(customerService.findById(1L)).thenReturn(customer);
            customerUseCase.delete(1L);
            verify(customerService).delete(customer);
            ArgumentCaptor<CustomerDeletedEvent> captor = ArgumentCaptor.forClass(CustomerDeletedEvent.class);
            verify(customerProducer).publishCustomerDeleted(captor.capture());
            CustomerDeletedEvent event = captor.getValue();
            assertEquals(1L, event.customerId());
        }
    }
}
