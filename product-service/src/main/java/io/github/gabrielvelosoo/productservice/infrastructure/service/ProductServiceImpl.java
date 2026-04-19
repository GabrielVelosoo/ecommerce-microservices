package io.github.gabrielvelosoo.productservice.infrastructure.service;

import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.ProductRepository;
import io.github.gabrielvelosoo.productservice.domain.service.ProductService;
import io.github.gabrielvelosoo.productservice.application.exception.RecordNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Product not found"));
    }

    @Override
    public Page<Product> findAll(Specification<Product> spec, Pageable pagination) {
        return productRepository.findAll(spec, pagination);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }
}
