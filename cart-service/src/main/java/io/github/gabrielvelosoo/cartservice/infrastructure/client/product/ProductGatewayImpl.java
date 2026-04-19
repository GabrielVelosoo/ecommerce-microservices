package io.github.gabrielvelosoo.cartservice.infrastructure.client.product;

import io.github.gabrielvelosoo.cartservice.application.dto.product.ProductSnapshotDTO;
import io.github.gabrielvelosoo.cartservice.application.gateway.ProductGateway;
import org.springframework.stereotype.Component;

@Component
public class ProductGatewayImpl implements ProductGateway {

    private final ProductFeignClient productFeignClient;

    public ProductGatewayImpl(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

    @Override
    public ProductSnapshotDTO findById(Long productId) {
        return productFeignClient.findById(productId);
    }
}
