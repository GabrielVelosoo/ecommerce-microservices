package io.github.gabrielvelosoo.customerservice.infrastructure.controller.customer;

import io.github.gabrielvelosoo.customerservice.application.dto.common.PageResponse;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.customer.CustomerUseCase;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidationOrder;
import io.github.gabrielvelosoo.customerservice.infrastructure.controller.GenericController;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.model.ErrorResponse;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.model.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers")
public class CustomerController implements GenericController {

    private static final Logger logger = LogManager.getLogger(CustomerController.class);

    private final CustomerUseCase customerUseCase;

    @PostMapping
    @Operation(
            summary = "Create a new customer",
            description = """
                Registers a new customer ensuring unique e-mail and CPF.
                Publishes an event to create the corresponding user in the authentication service.
                Returns the created customer data and the resource location.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class)
                    ),
                    headers = @Header(
                            name = "Location",
                            description = "URI of the newly created address resource"
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict — the e-mail or CPF provided is already registered with another account",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity — validation failed for one or more fields in the request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — an unexpected error occurred while communicating with Keycloak (e.g., creating, updating, or deleting a user, assigning roles, or validating tokens). Contact the development team if the issue persists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Validated(ValidationOrder.class) CustomerRequestDTO request) {
        logger.info("[HTTP POST /customers] Request received");
        CustomerResponseDTO response = customerUseCase.create(request);
        logger.info("[HTTP POST /customers] CREATED. customerId='{}'", response.id());
        URI location = generateHeaderLocation(response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Search customers by e-mail and/or CPF",
            description = """
                Retrieves a paginated list of customers filtered by e-mail and/or CPF.
                Only users with the ADMIN role can access this endpoint.
                If no filters are provided, all customers are returned with pagination applied.
                """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customers retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CustomerResponseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request — invalid query parameter or pagination value",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — missing, invalid or expired access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden — the authenticated user does not have permission to access this resource (role not allowed)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<PageResponse<CustomerResponseDTO>> searchCustomers(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "page-size", defaultValue = "10") Integer pageSize
    ) {
        PageResponse<CustomerResponseDTO> response = customerUseCase.searchCustomers(email, cpf, page, pageSize);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/me")
    @Operation(
            summary = "Edit logged customer",
            description = "Edit logged customer. Requires role USER or ADMIN. Publishes an event to edit the corresponding user in the authentication service. Returns the edited customer data.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer edited successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — invalid or expired access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity — validation failed for one or more fields in the request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — an unexpected error occurred while communicating with Keycloak (e.g., creating, updating, or deleting a user, assigning roles, or validating tokens). Contact the development team if the issue persists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CustomerResponseDTO> editLoggedCustomer(@RequestBody @Validated(ValidationOrder.class) CustomerUpdateDTO request) {
        logger.info("[HTTP PUT /customers/me] Request received");
        CustomerResponseDTO response = customerUseCase.editLoggedCustomer(request);
        logger.info("[HTTP PUT /customers/me] OK. customerId='{}'", response.id());
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    @Operation(
            summary = "Edit a customer",
            description = """
                Edit a customer.
                Requires role USER or ADMIN.
                Publishes an event to edit the corresponding user in the authentication service.
                Returns the updated customer data.
                """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer edited successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — invalid or expired access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden — the authenticated user does not have permission to access this resource (role not allowed)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found — no customer exists with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity — validation failed for one or more fields in the request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — an unexpected error occurred while communicating with Keycloak (e.g., creating, updating, or deleting a user, assigning roles, or validating tokens). Contact the development team if the issue persists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CustomerResponseDTO> edit(
            @PathVariable(name = "id") Long customerId,
            @RequestBody @Validated(ValidationOrder.class) CustomerUpdateDTO request
    ) {
        logger.info("[HTTP PUT /customers/{}] Request received", customerId);
        CustomerResponseDTO response = customerUseCase.edit(customerId, request);
        logger.info("[HTTP PUT /customers/{}] OK. customerId='{}'", customerId, response.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/me")
    @Operation(
            summary = "Delete logged customer",
            description = """
            Delete logged customer.
            Requires role USER or ADMIN.
            Publishes an event to delete the corresponding user in the authentication service.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Customer deleted successfully. No content is returned in the response body"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — invalid or expired access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — an unexpected error occurred while communicating with Keycloak (e.g., creating, updating, or deleting a user, assigning roles, or validating tokens). Contact the development team if the issue persists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> deleteLoggedCustomer() {
        logger.info("[HTTP DELETE /customers/me] Request received");
        customerUseCase.deleteLoggedCustomer();
        logger.info("[HTTP DELETE /customers/me] NO_CONTENT");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    @Operation(
            summary = "Delete a customer",
            description = """
            Delete a customer.
            Requires role ADMIN.
            Publishes an event to delete the corresponding user in the authentication service.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Customer deleted successfully. No content is returned in the response body"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — invalid or expired access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden — the authenticated user does not have permission to access this resource (role not allowed)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found — no customer exists with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — an unexpected error occurred while communicating with Keycloak (e.g., creating, updating, or deleting a user, assigning roles, or validating tokens). Contact the development team if the issue persists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long customerId) {
        logger.info("[HTTP DELETE /customers/{}] Request received", customerId);
        customerUseCase.delete(customerId);
        logger.info("[HTTP DELETE /customers/{}] NO_CONTENT", customerId);
        return ResponseEntity.noContent().build();
    }
}
