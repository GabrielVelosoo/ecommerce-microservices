package io.github.gabrielvelosoo.customerservice.application.usecase.address;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.mapper.AddressMapper;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.AddressValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.AddressService;
import io.github.gabrielvelosoo.customerservice.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddressUseCaseImpl implements AddressUseCase {

    private static final Logger logger = LogManager.getLogger(AddressUseCaseImpl.class);

    private final AddressService addressService;
    private final AuthService authService;
    private final AddressMapper addressMapper;
    private final AddressValidator addressValidator;

    @Override
    @Transactional
    public AddressResponseDTO create(AddressRequestDTO request) {
        logger.info("[CreateAddress] Starting address creation");
        Address address = addressMapper.toEntity(request);
        Customer customer = authService.getLoggedCustomer();
        address.setCustomer(customer);
        Address savedAddress = addressService.save(address);
        logger.info("[CreateAddress] Address successfully created. addressId='{}' userId='{}'", savedAddress.getId(), customer.getId());
        return addressMapper.toDTO(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesLoggedCustomer() {
        logger.info("[GetAddressesLoggedCustomer] Starting get addresses of logged customer");
        Customer customer = authService.getLoggedCustomer();
        List<Address> addresses = addressService.getAddressesLoggedCustomer(customer.getId());
        logger.info("[GetAddressesLoggedCustomer] Successfully retrieved '{}' addresses. customerId='{}'", addresses.size(), customer.getId());
        return addressMapper.toDTOs(addresses);
    }

    @Override
    @Transactional
    public AddressResponseDTO edit(Long addressId, AddressRequestDTO request) {
        logger.info("[EditAddress] Starting address edition. addressId='{}'", addressId);
        Address address = addressService.findById(addressId);
        addressValidator.validateOnUpdateAndDelete(address);
        addressMapper.edit(address, request);
        Address editedAddress = addressService.save(address);
        logger.info("[EditAddress] AddressId='{}' edited successfully", editedAddress.getId());
        return addressMapper.toDTO(editedAddress);
    }

    @Override
    @Transactional
    public void delete(Long addressId) {
        logger.info("[DeleteAddress] Starting address deletion. addressId='{}'", addressId);
        Address address = addressService.findById(addressId);
        addressValidator.validateOnUpdateAndDelete(address);
        addressService.delete(address);
        logger.info("[DeleteAddress] AddressId='{}' deleted successfully", addressId);
    }
}
