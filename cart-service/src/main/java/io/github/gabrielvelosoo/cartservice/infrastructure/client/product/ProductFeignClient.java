package io.github.gabrielvelosoo.cartservice.infrastructure.client.product;

import io.github.gabrielvelosoo.cartservice.application.dto.product.ProductSnapshotDTO;
import io.github.gabrielvelosoo.cartservice.infrastructure.configuration.feign.ProductFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        url = "${services.product.url}",
        configuration = ProductFeignConfiguration.class
)
public interface ProductFeignClient {

    @GetMapping("/api/v1/internal/products/{id}")
    ProductSnapshotDTO findById(@PathVariable("id") Long productId);
}
