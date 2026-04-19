package io.github.gabrielvelosoo.orderservice.infrastructure.controller;

import io.github.gabrielvelosoo.orderservice.application.usecase.CancelOrderUseCase;
import io.github.gabrielvelosoo.orderservice.application.usecase.RemoveOrderItemUseCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/orders")
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    private final RemoveOrderItemUseCase removeOrderItem;
    private final CancelOrderUseCase cancelOrder;

    public OrderController(RemoveOrderItemUseCase removeOrderItem, CancelOrderUseCase cancelOrder) {
        this.removeOrderItem = removeOrderItem;
        this.cancelOrder = cancelOrder;
    }

    @DeleteMapping(value = "/{orderId}/items/{productId}")
    public ResponseEntity<Void> removeOrderItem(
            @PathVariable(name = "orderId") Long orderId,
            @PathVariable(name = "productId") Long productId
    ) {
        logger.info("[HTTP DELETE /orders/{}/items/{}] Request received", orderId, productId);
        removeOrderItem.execute(orderId, productId);
        logger.info("[HTTP DELETE /orders/{}/items/{}] NO_CONTENT", orderId, productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable(name = "orderId") Long orderId) {
        logger.info("[HTTP POST /orders/{}/cancel] Request received", orderId);
        cancelOrder.execute(orderId);
        logger.info("[HTTP POST /orders/{}/cancel] NO_CONTENT", orderId);
        return ResponseEntity.noContent().build();
    }
}
