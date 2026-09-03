package vn.edu.crs.api_gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.edu.crs.api_gateway.service.ApiKeyValidationCache;
import vn.edu.crs.api_gateway.service.AuthServiceClient;

@Component
@RequiredArgsConstructor
public class ApiKeyFilter implements GlobalFilter, Ordered {

    private final AuthServiceClient authServiceClient;
    private final ApiKeyValidationCache validationCache;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (!("/api/public/courses".equals(path)
                || path.startsWith("/api/public/courses/"))) {
            return chain.filter(exchange);
        }
        if (request.getMethod() != HttpMethod.GET) {
            return forbidden(exchange);
        }

        String requiredScope = requiredScope(path);

        String apiKey = request.getHeaders().getFirst("X-API-KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return forbidden(exchange);
        }

        Boolean cachedResult = validationCache.get(apiKey, requiredScope);
        if (cachedResult != null) {
            return cachedResult ? chain.filter(exchange) : forbidden(exchange);
        }

        return authServiceClient.validateApiKey(apiKey, requiredScope)
                .flatMap(valid -> {
                    validationCache.put(apiKey, requiredScope, valid);
                    return valid ? chain.filter(exchange) : forbidden(exchange);
                });
    }

    private String requiredScope(String path) {
        if ("/api/public/courses".equals(path)) {
            return "courses:read";
        }
        return "courses:read-detail";
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
