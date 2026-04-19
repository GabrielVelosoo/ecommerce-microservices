package io.github.gabrielvelosoo.cartservice.infrastructure.configuration.feign;

import feign.RequestInterceptor;
import io.github.gabrielvelosoo.cartservice.infrastructure.service.auth.KeycloakClientCredentialsTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderFeignConfiguration {

    private final KeycloakClientCredentialsTokenProvider tokenProvider;

    public OrderFeignConfiguration(KeycloakClientCredentialsTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public RequestInterceptor orderFeignRequestInterceptor() {
        return requestTemplate ->
                requestTemplate.header(
                        "Authorization",
                        "Bearer " + tokenProvider.getToken()
                );
    }
}
