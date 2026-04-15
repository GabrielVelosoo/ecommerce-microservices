package io.github.gabrielvelosoo.productservice.application.usecase.product;

import io.github.gabrielvelosoo.productservice.application.dto.common.PageResponse;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductCreateDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductFilterDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductUpdateDTO;

import java.io.IOException;

public interface ProductUseCase {

    ProductResponseDTO create(ProductCreateDTO productCreateDTO) throws IOException;
    ProductResponseDTO findById(Long productId);
    PageResponse<ProductResponseDTO> getByFilter(ProductFilterDTO productFilterDTO, Integer page, Integer pageSize);
    ProductResponseDTO update(Long productId, ProductUpdateDTO productUpdateDTO) throws IOException;
    void delete(Long productId) throws IOException;
}
