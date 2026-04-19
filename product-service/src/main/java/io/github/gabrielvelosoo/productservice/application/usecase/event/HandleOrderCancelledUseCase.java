package io.github.gabrielvelosoo.productservice.application.usecase.event;

import io.github.gabrielvelosoo.productservice.application.dto.event.OrderCancelledEvent;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.domain.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class HandleOrderCancelledUseCase {

    private static final Logger logger = LogManager.getLogger(HandleOrderCancelledUseCase.class);

    private final ProductService productService;

    public HandleOrderCancelledUseCase(ProductService productService) {
        this.productService = productService;
    }

    public void execute(OrderCancelledEvent event) {
        logger.info("[OrderCancelledEvent] Restoring stock. orderId='{}'", event.orderId());
        event.items().forEach(item -> {
            logger.debug("[OrderCancelledEvent] Restoring stock. orderId='{}', productId='{}', quantity='{}'",
                    event.orderId(), item.productId(), item.quantity());
            Product product = productService.findById(item.productId());
            product.increaseStock(item.quantity());
            productService.save(product);
        });
        logger.info("[OrderCancelledEvent] Stock restored. orderId='{}'", event.orderId());
    }
}
