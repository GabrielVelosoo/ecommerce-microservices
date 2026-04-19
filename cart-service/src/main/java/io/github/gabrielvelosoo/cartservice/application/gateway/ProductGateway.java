package io.github.gabrielvelosoo.cartservice.application.gateway;

import io.github.gabrielvelosoo.cartservice.application.dto.product.ProductSnapshotDTO;

public interface ProductGateway {

    ProductSnapshotDTO findById(Long productId);
}
