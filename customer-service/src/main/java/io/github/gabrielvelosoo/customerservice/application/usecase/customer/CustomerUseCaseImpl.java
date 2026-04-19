package io.github.gabrielvelosoo.customerservice.application.usecase.customer;

import io.github.gabrielvelosoo.customerservice.application.dto.common.PageResponse;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.application.mapper.CustomerMapper;
import io.github.gabrielvelosoo.customerservice.application.mapper.PageMapper;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.CustomerValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.AuthService;
import io.github.gabrielvelosoo.customerservice.domain.service.CustomerService;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.producer.CustomerProducer;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static io.github.gabrielvelosoo.customerservice.infrastructure.persistence.specification.CustomerSpecification.createSpecification;

@Component
@RequiredArgsConstructor
public class CustomerUseCaseImpl implements CustomerUseCase {

    private static final Logger logger = LogManager.getLogger(CustomerUseCaseImpl.class);

    private final CustomerService customerService;
    private final AuthService authService;
    private final CustomerMapper customerMapper;
    private final CustomerValidator customerValidator;
    private final CustomerProducer customerProducer;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO request) {
        logger.info("[CreateCustomer] Starting customer creation. email='{}'", request.email());
        Customer customer = customerMapper.toEntity(request);
        customerValidator.validateOnCreate(customer);
        Customer savedCustomer = customerService.save(customer);
        customerProducer.publishCustomerCreated(
                new CustomerCreatedEvent(
                        savedCustomer.getId(),
                        savedCustomer.getName(),
                        savedCustomer.getLastName(),
                        request.email(),
                        request.password()
                )
        );
        logger.info("[CreateCustomer] Customer successfully created. customerId='{}'", savedCustomer.getId());
        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponseDTO> searchCustomers(String email, String cpf, Integer page, Integer pageSize) {
        Specification<Customer> specs = createSpecification(email, cpf);
        Pageable pagination = pagination(page, pageSize);
        Page<Customer> customers = customerService.findCustomers(specs, pagination);
        return pageMapper.toPageResponse(customers, customerMapper::toDTO);
    }

    private Pageable pagination(Integer page, Integer pageSize) {
        return PageRequest.of(page, pageSize);
    }

    @Override
    @Transactional
    public CustomerResponseDTO editLoggedCustomer(CustomerUpdateDTO request) {
        Customer customer = authService.getLoggedCustomer();
        return performCustomerEdition(customer, request);
    }

    @Override
    @Transactional
    public CustomerResponseDTO edit(Long customerId, CustomerUpdateDTO request) {
        Customer customer = customerService.findById(customerId);
        return performCustomerEdition(customer, request);
    }

    private CustomerResponseDTO performCustomerEdition(Customer customer, CustomerUpdateDTO updateDTO) {
        logger.info("[EditCustomer] Starting customer edition. customerId='{}'", customer.getId());
        customerValidator.validateOnUpdate(customer.getId(), updateDTO);
        customerMapper.edit(customer, updateDTO);
        Customer editedCustomer = customerService.save(customer);
        customerProducer.publishCustomerUpdated(
                new CustomerUpdatedEvent(
                        editedCustomer.getId(),
                        editedCustomer.getName(),
                        editedCustomer.getLastName()
                )
        );
        logger.info("[EditCustomer] CustomerId='{}' edited successfully", editedCustomer.getId());
        return customerMapper.toDTO(editedCustomer);
    }

    @Override
    @Transactional
    public void deleteLoggedCustomer() {
        Customer customer = authService.getLoggedCustomer();
        performCustomerDeletion(customer);
    }

    @Override
    @Transactional
    public void delete(Long customerId) {
        Customer customer = customerService.findById(customerId);
        performCustomerDeletion(customer);
    }

    private void performCustomerDeletion(Customer customer) {
        logger.info("[DeleteCustomer] Starting customer deletion. customerId='{}'", customer.getId());
        Long customerId = customer.getId();
        customerService.delete(customer);
        customerProducer.publishCustomerDeleted(
                new CustomerDeletedEvent(
                        customerId
                )
        );
        logger.info("[DeleteCustomer] CustomerId='{}' deleted successfully", customerId);
    }
}
