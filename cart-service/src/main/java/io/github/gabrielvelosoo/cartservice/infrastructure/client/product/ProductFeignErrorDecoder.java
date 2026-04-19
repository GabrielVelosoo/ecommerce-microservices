package io.github.gabrielvelosoo.cartservice.infrastructure.client.product;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.github.gabrielvelosoo.cartservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.cartservice.application.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.cartservice.application.exception.UnauthorizedException;

public class ProductFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new RecordNotFoundException("Product not found");
            case 400, 422 -> new BusinessException("Invalid product request");
            case 401 -> new UnauthorizedException("Unauthorized access to Product Service");
            case 403 -> new UnauthorizedException("Forbidden access to Product Service");
            default -> new BusinessException(
                    "Error calling Product Service. Status: " + response.status()
            );
        };
    }
}
