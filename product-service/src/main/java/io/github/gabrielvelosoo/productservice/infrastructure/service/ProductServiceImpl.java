package io.github.gabrielvelosoo.productservice.infrastructure.service;

import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.ProductRepository;
import io.github.gabrielvelosoo.productservice.domain.service.ProductService;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.RecordNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

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
        logger.debug("Searching for product id='{}'", id);
        return productRepository.findById(id)
                .orElseThrow( () -> {
                    logger.warn("Product not found id='{}'", id);
                    return new RecordNotFoundException("Product not found");
                } );
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
