package io.github.gabrielvelosoo.productservice.infrastructure.persistence.specification;

import io.github.gabrielvelosoo.productservice.application.dto.product.ProductFilterDTO;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> createSpecification(ProductFilterDTO filter) {
        Specification<Product> specs = Specification.allOf();
        if(filter.name() != null && !filter.name().isBlank()) {
            specs = specs.and(nameLike(filter.name()));
        }
        if(filter.minPrice() != null) {
            specs = specs.and(priceGreaterThanOrEqualTo(filter.minPrice()));
        }
        if(filter.maxPrice() != null) {
            specs = specs.and(priceLessThanOrEqualTo(filter.maxPrice()));
        }
        if(filter.minStock() != null) {
            specs = specs.and(stockGreaterThanOrEqualTo(filter.minStock()));
        }
        if(filter.maxStock() != null) {
            specs = specs.and(stockLessThanOrEqualTo(filter.maxStock()));
        }
        if(filter.categoryId() != null) {
            specs = specs.and(categoryEquals(filter.categoryId()));
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
