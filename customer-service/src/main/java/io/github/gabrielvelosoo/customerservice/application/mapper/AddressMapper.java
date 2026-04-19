package io.github.gabrielvelosoo.customerservice.application.mapper;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AddressMapper {

    public abstract Address toEntity(AddressRequestDTO request);
    public abstract AddressResponseDTO toDTO(Address address);
    public abstract List<AddressResponseDTO> toDTOs(List<Address> addresses);

    public void edit(Address address, AddressRequestDTO request) {
        address.setContactName(request.contactName());
        address.setContactLastName(request.contactLastName());
        address.setContactPhone(request.contactPhone());
        address.setAddress(request.address());
        address.setNumber(request.number());
        address.setNeighborhood(request.neighborhood());
        address.setCity(request.city());
        address.setState(request.state());
        address.setCep(request.cep());
        address.setComplement(request.complement());
    }
}
