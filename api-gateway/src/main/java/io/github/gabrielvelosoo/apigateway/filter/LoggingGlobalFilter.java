package io.github.gabrielvelosoo.apigateway.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class LoggingGlobalFilter implements GlobalFilter {

    private static final Logger logger = LogManager.getLogger(LoggingGlobalFilter.class);
    private static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx)
                .doOnEach(signal -> {
                    if(!signal.isOnComplete() && !signal.isOnError()) return;
                    signal.getContextView()
                            .getOrEmpty(CORRELATION_ID)
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .ifPresent(id -> MDC.put(CORRELATION_ID, id));
                })
                .doOnSuccess(v -> logger.info(
                        "[GATEWAY] {} {} -> {}",
                        exchange.getRequest().getMethod(),
                        exchange.getRequest().getURI().getPath(),
                        exchange.getResponse().getStatusCode()
                ))
                .doFinally(signal -> MDC.clear());
    }
}
