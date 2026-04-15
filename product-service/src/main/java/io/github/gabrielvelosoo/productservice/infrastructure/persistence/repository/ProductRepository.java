package io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository;

import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {
}
