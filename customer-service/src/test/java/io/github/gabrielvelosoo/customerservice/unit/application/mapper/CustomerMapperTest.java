package io.github.gabrielvelosoo.customerservice.unit.application.mapper;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.mapper.CustomerMapper;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerMapperTest {

    CustomerMapper customerMapper;

    @BeforeEach
    void setUp() {
        customerMapper = Mappers.getMapper(CustomerMapper.class);
    }

    @Test
    void shouldEditCustomerWithUpdateDTOSuccessfully() {
        Customer customer = new Customer();
        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO(
                "Gabriel",
                "Veloso",
                "97535472823",
                LocalDate.of(2004, 4, 10)
        );
        customerMapper.edit(customer, updateDTO);
        assertEquals("Gabriel", customer.getName());
        assertEquals("Veloso", customer.getLastName());
        assertEquals("97535472823", customer.getCpf());
        assertEquals(LocalDate.of(2004, 4, 10), customer.getBirthDate());
    }
}