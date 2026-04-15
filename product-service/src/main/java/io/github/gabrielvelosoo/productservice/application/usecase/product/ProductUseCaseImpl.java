package io.github.gabrielvelosoo.productservice.application.usecase.product;

import io.github.gabrielvelosoo.productservice.application.dto.common.PageResponse;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductCreateDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductFilterDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductUpdateDTO;
import io.github.gabrielvelosoo.productservice.application.mapper.PageMapper;
import io.github.gabrielvelosoo.productservice.application.mapper.ProductMapper;
import io.github.gabrielvelosoo.productservice.application.validator.custom.ProductValidator;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.domain.service.ImageStorageService;
import io.github.gabrielvelosoo.productservice.domain.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static io.github.gabrielvelosoo.productservice.infrastructure.persistence.specification.ProductSpecification.createSpecification;

@Service
public class ProductUseCaseImpl implements ProductUseCase {

    private static final Logger logger = LogManager.getLogger(ProductUseCaseImpl.class);

    private final ProductService productService;
    private final ProductValidator productValidator;
    private final ImageStorageService imageStorageService;
    private final ProductMapper productMapper;
    private final PageMapper pageMapper;

    public ProductUseCaseImpl(ProductService productService, ProductValidator productValidator, ImageStorageService imageStorageService, ProductMapper productMapper, PageMapper pageMapper) {
        this.productService = productService;
        this.productValidator = productValidator;
        this.imageStorageService = imageStorageService;
        this.productMapper = productMapper;
        this.pageMapper = pageMapper;
    }

    @Override
    @Transactional
    public ProductResponseDTO create(ProductCreateDTO productCreateDTO) throws IOException {
        logger.debug(
                "Starting product creation. Product name='{}', Category id='{}'",
                productCreateDTO.name(),
                productCreateDTO.categoryId()
        );
        Product product = productMapper.toEntity(productCreateDTO);
        productValidator.validateOnCreateAndUpdate(product);
        String imageUrl = imageStorageService.save(productCreateDTO.image());
        logger.debug("Image saved successfully. Generated URL='{}'", imageUrl);
        product.setImageUrl(imageUrl);
        Product createdProduct = productService.save(product);
        return productMapper.toDTO(createdProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long productId) {
        logger.debug("Find product by id='{}'", productId);
        Product product = productService.findById(productId);
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDTO> getByFilter(ProductFilterDTO productFilterDTO, Integer page, Integer pageSize) {
        logger.debug("Starting product filtering. Filters='{}', page='{}', pageSize='{}'",
                productFilterDTO, page, pageSize);
        Specification<Product> specs = createSpecification(productFilterDTO);
        Pageable pagination = pagination(page, pageSize);
        Page<Product> result = productService.findAll(specs, pagination);
        return pageMapper.toPageResponse(result, productMapper::toDTO);
    }

    private Pageable pagination(Integer page, Integer pageSize) {
        return PageRequest.of(page, pageSize);
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long productId, ProductUpdateDTO productUpdateDTO) throws IOException {
        logger.debug("Starting update of product id='{}'", productId);
        Product product = productService.findById(productId);
        logger.debug("Product found: id='{}', current name='{}'", product.getId(), product.getName());
        productValidator.validateOnCreateAndUpdate(product);
        logger.debug("Applying updates from DTO to product...");
        productMapper.update(product, productUpdateDTO);
        Product updatedProduct = productService.save(product);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void delete(Long productId) throws IOException {
        logger.debug("Starting deletion of product id='{}'", productId);
        Product product = productService.findById(productId);
        logger.debug("Product found for deletion: id='{}', name='{}'", product.getId(), product.getName());
        logger.debug("Deleting image file='{}'", product.getImageUrl());
        imageStorageService.delete(product.getImageUrl());
        productService.delete(product);
    }
}
