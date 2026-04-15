package io.github.gabrielvelosoo.productservice.domain.service;

import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {

    Product save(Product product);
    Product findById(Long id);
    Page<Product> findAll(Specification<Product> spec, Pageable pagination);
    void delete(Product product);
}
