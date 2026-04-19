package io.github.gabrielvelosoo.cartservice.infrastructure.configuration.feign;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import io.github.gabrielvelosoo.cartservice.infrastructure.client.product.ProductFeignErrorDecoder;
import io.github.gabrielvelosoo.cartservice.infrastructure.service.auth.KeycloakClientCredentialsTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductFeignConfiguration {

    private final KeycloakClientCredentialsTokenProvider tokenProvider;

    public ProductFeignConfiguration(KeycloakClientCredentialsTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public RequestInterceptor productFeignRequestInterceptor() {
        return requestTemplate ->
                requestTemplate.header(
                        "Authorization",
                        "Bearer " + tokenProvider.getToken()
                );
    }

    @Bean
    public ErrorDecoder productErrorDecoder() {
        return new ProductFeignErrorDecoder();
    }
}
