package io.github.gabrielvelosoo.cartservice.application.dto.product;

import java.math.BigDecimal;

public record ProductSnapshotDTO(
        Long id,
        BigDecimal price,
        Integer stockQuantity
    ) {
}
