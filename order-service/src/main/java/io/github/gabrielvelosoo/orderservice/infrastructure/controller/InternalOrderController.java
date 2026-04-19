package io.github.gabrielvelosoo.orderservice.infrastructure.controller;

import io.github.gabrielvelosoo.orderservice.application.dto.order.CreateOrderFromCartRequestDTO;
import io.github.gabrielvelosoo.orderservice.application.dto.order.OrderResponseDTO;
import io.github.gabrielvelosoo.orderservice.application.usecase.CreateOrderFromCartUseCase;
import io.github.gabrielvelosoo.orderservice.application.validator.group.ValidationOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/internal/orders")
public class InternalOrderController {

    private static final Logger logger = LogManager.getLogger(InternalOrderController.class);

    private final CreateOrderFromCartUseCase createOrderFromCart;

    public InternalOrderController(CreateOrderFromCartUseCase createOrderFromCart) {
        this.createOrderFromCart = createOrderFromCart;
    }

    @PostMapping(value = "/from-cart")
    @PreAuthorize("hasRole('ORDER_CREATE')")
    public ResponseEntity<OrderResponseDTO> createOrderFromCart(@RequestBody @Validated(ValidationOrder.class) CreateOrderFromCartRequestDTO request) {
        logger.info("[HTTP POST /orders/from-cart] userId='{}'", request.userId());
        OrderResponseDTO response = createOrderFromCart.execute(request);
        logger.info("[HTTP POST /orders/from-cart] CREATED orderId='{}'", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
