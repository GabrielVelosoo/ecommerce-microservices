package io.github.gabrielvelosoo.productservice.application.usecase.event;

import io.github.gabrielvelosoo.productservice.application.dto.event.OrderItemRemovedEvent;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.domain.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class HandleOrderItemRemovedUseCase {

    private static final Logger logger = LogManager.getLogger(HandleOrderItemRemovedUseCase.class);

    private final ProductService productService;

    public HandleOrderItemRemovedUseCase(ProductService productService) {
        this.productService = productService;
    }

    public void execute(OrderItemRemovedEvent event) {
        logger.info("[OrderItemRemovedEvent] Restoring stock for removed item. orderId='{}'", event.orderId());
        if(event.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        Product product = productService.findById(event.productId());
        product.increaseStock(event.quantity());
        productService.save(product);
        logger.info("[OrderItemRemovedEvent] Stock restored for removed item. orderId='{}'", event.orderId());
    }
}
