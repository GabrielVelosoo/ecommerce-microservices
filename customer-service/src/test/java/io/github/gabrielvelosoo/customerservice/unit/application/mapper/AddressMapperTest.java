package io.github.gabrielvelosoo.customerservice.unit.application.mapper;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.mapper.AddressMapper;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressMapperTest {

    AddressMapper addressMapper;

    @BeforeEach
    void setUp() {
        addressMapper = Mappers.getMapper(AddressMapper.class);
    }

    @Test
    void shouldEditAddressSuccessfully() {
        Address address = new Address();
        AddressRequestDTO requestDTO = new AddressRequestDTO(
                "Gabriel",
                "Veloso",
                "11999999999",
                "Rua A",
                "123",
                "Centro",
                "Brasília",
                "DF",
                "01000-000",
                "Apto 10"
        );
        addressMapper.edit(address, requestDTO);
        assertEquals("Gabriel", address.getContactName());
        assertEquals("Veloso", address.getContactLastName());
        assertEquals("11999999999", address.getContactPhone());
        assertEquals("Rua A", address.getAddress());
        assertEquals("123", address.getNumber());
        assertEquals("Centro", address.getNeighborhood());
        assertEquals("Brasília", address.getCity());
        assertEquals("DF", address.getState());
        assertEquals("01000-000", address.getCep());
        assertEquals("Apto 10", address.getComplement());
    }
}