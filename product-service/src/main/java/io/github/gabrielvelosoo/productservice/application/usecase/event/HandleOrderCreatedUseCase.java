package io.github.gabrielvelosoo.productservice.application.usecase.event;

import io.github.gabrielvelosoo.productservice.application.dto.event.OrderCreatedEvent;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.domain.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HandleOrderCreatedUseCase {

    private static final Logger logger = LogManager.getLogger(HandleOrderCreatedUseCase.class);

    private final ProductService productService;

    public HandleOrderCreatedUseCase(ProductService productService) {
        this.productService = productService;
    }

    @Transactional
    public void execute(OrderCreatedEvent event) {
        logger.info("[OrderCreatedEvent] Reserving stock. orderId='{}'", event.orderId());
        event.items().forEach(item -> {
            logger.debug("[OrderCreatedEvent] Reserving stock. orderId='{}', productId='{}', quantity='{}'",
                    event.orderId(), item.productId(), item.quantity());
            Product product = productService.findById(item.productId());
            product.decreaseStock(item.quantity());
            productService.save(product);
        });
        logger.info("[OrderCreatedEvent] Stock reserved successfully. orderId='{}'", event.orderId());
    }
}
