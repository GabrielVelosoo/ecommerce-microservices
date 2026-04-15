package io.github.gabrielvelosoo.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdGlobalFilter implements GlobalFilter {

    private static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID);
        if(correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        String finalCorrelationId = correlationId;
        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header(CORRELATION_ID, finalCorrelationId)
                .build();
        return chain.filter(exchange.mutate().request(request).build())
                .contextWrite(ctx -> ctx.put(CORRELATION_ID, finalCorrelationId));
    }
}
