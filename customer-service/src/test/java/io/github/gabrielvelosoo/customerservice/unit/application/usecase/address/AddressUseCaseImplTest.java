package io.github.gabrielvelosoo.customerservice.unit.application.usecase.address;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.mapper.AddressMapper;
import io.github.gabrielvelosoo.customerservice.application.usecase.address.AddressUseCaseImpl;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.AddressValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.AddressService;
import io.github.gabrielvelosoo.customerservice.domain.service.AuthService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressUseCaseImplTest {

    @Mock
    AddressService addressService;

    @Mock
    AuthService authService;

    @Mock
    AddressMapper addressMapper;

    @Mock
    AddressValidator addressValidator;

    @InjectMocks
    AddressUseCaseImpl addressUseCase;

    @Nested
    class CreateTests {

        @Test
        void shouldCreateAddressSuccessfully() {
            AddressRequestDTO requestDTO = mock(AddressRequestDTO.class);
            Address address = new Address();
            Address savedAddress = new Address();
            Customer customer = new Customer();
            customer.setId(1L);
            AddressResponseDTO responseDTO = mock(AddressResponseDTO.class);
            when(addressMapper.toEntity(requestDTO)).thenReturn(address);
            when(authService.getLoggedCustomer()).thenReturn(customer);
            when(addressService.save(address)).thenReturn(savedAddress);
            when(addressMapper.toDTO(savedAddress)).thenReturn(responseDTO);
            AddressResponseDTO result = addressUseCase.create(requestDTO);
            assertNotNull(result);
            assertEquals(responseDTO, result);
            assertEquals(customer, address.getCustomer());
            verify(authService).getLoggedCustomer();
            verify(addressMapper).toEntity(requestDTO);
            verify(addressService).save(address);
            verify(addressMapper).toDTO(savedAddress);
        }
    }

    @Nested
    class GetAddressesLoggedCustomerTests {

        @Test
        void shouldGetAddressesLoggedCustomerSuccessfully() {
            Customer customer = new Customer();
            customer.setId(1L);
            Address address = new Address();
            AddressResponseDTO responseDTO = mock(AddressResponseDTO.class);
            List<Address> addresses = List.of(address);
            List<AddressResponseDTO> responseDTOs = List.of(responseDTO);
            when(authService.getLoggedCustomer()).thenReturn(customer);
            when(addressService.getAddressesLoggedCustomer(1L)).thenReturn(addresses);
            when(addressMapper.toDTOs(addresses)).thenReturn(responseDTOs);
            List<AddressResponseDTO> result = addressUseCase.getAddressesLoggedCustomer();
            assertEquals(1, result.size());
            assertEquals(responseDTO, result.getFirst());
            verify(authService).getLoggedCustomer();
            verify(addressService).getAddressesLoggedCustomer(1L);
            verify(addressMapper).toDTOs(addresses);
        }
    }

    @Nested
    class EditTests {

        @Test
        void shouldEditAddressSuccessfully() {
            AddressRequestDTO requestDTO = mock(AddressRequestDTO.class);
            Address address = new Address();
            AddressResponseDTO responseDTO = mock(AddressResponseDTO.class);
            when(addressService.findById(1L)).thenReturn(address);
            when(addressService.save(address)).thenReturn(address);
            when(addressMapper.toDTO(address)).thenReturn(responseDTO);
            AddressResponseDTO result = addressUseCase.edit(1L, requestDTO);
            assertNotNull(result);
            assertEquals(responseDTO, result);
            InOrder inOrder = inOrder(addressService, addressValidator, addressMapper);
            inOrder.verify(addressService).findById(1L);
            inOrder.verify(addressValidator).validateOnUpdateAndDelete(address);
            inOrder.verify(addressMapper).edit(address, requestDTO);
            inOrder.verify(addressService).save(address);
            inOrder.verify(addressMapper).toDTO(address);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteAddressSuccessfully() {
            Address address = new Address();
            when(addressService.findById(1L)).thenReturn(address);
            addressUseCase.delete(1L);
            verify(addressService).findById(1L);
            verify(addressValidator).validateOnUpdateAndDelete(address);
            verify(addressService).delete(address);
        }
    }
}