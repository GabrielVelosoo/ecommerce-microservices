package io.github.gabrielvelosoo.productservice.infrastructure.persistence.specification;

import io.github.gabrielvelosoo.productservice.application.dto.product.ProductFilterDTO;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    private static final Logger logger = LogManager.getLogger(ProductSpecification.class);

    public static Specification<Product> createSpecification(ProductFilterDTO dto) {
        logger.debug("Building specification for filters: {}", dto);
        Specification<Product> specs = Specification.allOf();
        if(dto.name() != null && !dto.name().isBlank()) {
            logger.trace("Applying filter: name LIKE '{}'", dto.name());
            specs = specs.and(nameLike(dto.name()));
        }
        if(dto.minPrice() != null) {
            logger.trace("Applying filter: price >= '{}'", dto.minPrice());
            specs = specs.and(priceGreaterThanOrEqualTo(dto.minPrice()));
        }
        if(dto.maxPrice() != null) {
            logger.trace("Applying filter: price <= '{}'", dto.maxPrice());
            specs = specs.and(priceLessThanOrEqualTo(dto.maxPrice()));
        }
        if(dto.minStock() != null) {
            logger.trace("Applying filter: stock >= '{}'", dto.minStock());
            specs = specs.and(stockGreaterThanOrEqualTo(dto.minStock()));
        }
        if(dto.maxStock() != null) {
            logger.trace("Applying filter: stock <= '{}'", dto.maxStock());
            specs = specs.and(stockLessThanOrEqualTo(dto.maxStock()));
        }
        if(dto.categoryId() != null) {
            logger.trace("Applying filter: categoryId = '{}'", dto.categoryId());
            specs = specs.and(categoryEquals(dto.categoryId()));
        }
        return specs;
    }

    private static Specification<Product> nameLike(String name) {
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase() + "%");
    }

    private static Specification<Product> priceGreaterThanOrEqualTo(BigDecimal minPrice) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private static Specification<Product> priceLessThanOrEqualTo(BigDecimal maxPrice) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    private static Specification<Product> stockGreaterThanOrEqualTo(Integer minStock) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("stockQuantity"), minStock);
    }

    private static Specification<Product> stockLessThanOrEqualTo(Integer maxStock) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("stockQuantity"), maxStock);
    }

    private static Specification<Product> categoryEquals(Long categoryId) {
        return (root, query, cb) ->
                cb.equal(root.get("category").get("id"), categoryId);
    }
}
