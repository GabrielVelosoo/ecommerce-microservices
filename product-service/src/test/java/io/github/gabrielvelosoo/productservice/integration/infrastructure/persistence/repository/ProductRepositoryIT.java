package io.github.gabrielvelosoo.productservice.integration.infrastructure.persistence.repository;

import io.github.gabrielvelosoo.productservice.application.dto.product.ProductFilterDTO;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.ProductRepository;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.specification.ProductSpecification;
import io.github.gabrielvelosoo.productservice.integration.configuration.AbstractIT;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryIT extends AbstractIT {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void clear() {
        entityManager.clear();
    }

    @Nested
    class SpecificationTests {

        @Test
        void shouldReturnAllProductsWhenFilterIsEmpty() {
            Category category = persistCategory("General");
            persistProduct("A", BigDecimal.TEN, 10, category);
            persistProduct("B", BigDecimal.ONE, 5, category);
            ProductFilterDTO filter = new ProductFilterDTO(
                    null, null, null, null, null, null
            );
            Specification<Product> spec = ProductSpecification.createSpecification(filter);
            assertThat(productRepository.findAll(spec)).hasSize(2);
        }

        @Test
        void shouldFindProductsByNameLike() {
            Category category = persistCategory("Electronics");
            persistProduct("Notebook Gamer", BigDecimal.valueOf(5000), 10, category);
            persistProduct("Mouse Gamer", BigDecimal.valueOf(200), 50, category);
            persistProduct("Keyboard", BigDecimal.valueOf(300), 20, category);
            ProductFilterDTO filter = new ProductFilterDTO(
                    "gamer", null, null, null, null, null
            );
            Specification<Product> spec = ProductSpecification.createSpecification(filter);
            List<Product> result = productRepository.findAll(spec);
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(Product::getName)
                    .containsExactlyInAnyOrder("Notebook Gamer", "Mouse Gamer");
        }

        @Test
        void shouldFilterByPriceRangeAndStock() {
            Category category = persistCategory("Books");
            persistProduct("Book A", BigDecimal.valueOf(50), 5, category);
            persistProduct("Book B", BigDecimal.valueOf(100), 15, category);
            persistProduct("Book C", BigDecimal.valueOf(150), 25, category);
            ProductFilterDTO filter = new ProductFilterDTO(
                    null,
                    BigDecimal.valueOf(80),
                    BigDecimal.valueOf(160),
                    10,
                    30,
                    null
            );
            Specification<Product> spec = ProductSpecification.createSpecification(filter);
            List<Product> result = productRepository.findAll(spec);
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(Product::getName)
                    .containsExactlyInAnyOrder("Book B", "Book C");
        }

        @Test
        void shouldFilterByCategoryId() {
            Category electronics = persistCategory("Electronics");
            Category books = persistCategory("Books");
            persistProduct("TV", BigDecimal.valueOf(3000), 5, electronics);
            persistProduct("Notebook", BigDecimal.valueOf(5000), 3, electronics);
            persistProduct("Book", BigDecimal.valueOf(100), 20, books);
            ProductFilterDTO filter = new ProductFilterDTO(
                    null, null, null, null, null, electronics.getId()
            );
            Specification<Product> spec = ProductSpecification.createSpecification(filter);
            List<Product> result = productRepository.findAll(spec);
            assertThat(result).hasSize(2);
            assertThat(result)
                    .allMatch(p -> p.getCategory().getId().equals(electronics.getId()));
        }
    }

    private Category persistCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(category.generateSlug(name));
        entityManager.persist(category);
        entityManager.flush();
        return category;
    }

    private void persistProduct(
            String name,
            BigDecimal price,
            Integer stockQuantity,
            Category category
    ) {
        Product product = new Product(
                name,
                "DESC",
                price,
                stockQuantity,
                category
        );
        entityManager.persist(product);
        entityManager.flush();
    }
}