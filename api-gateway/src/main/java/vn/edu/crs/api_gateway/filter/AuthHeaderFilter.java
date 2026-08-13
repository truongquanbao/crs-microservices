package vn.edu.crs.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthHeaderFilter implements GlobalFilter, Ordered {

    // Những endpoint không cần Authorization
    private static final List<String> OPEN_PATHS = List.of(
            "/api/auth/login",
            "/api/public/courses"
    );

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();

        // Endpoint mở
        boolean isOpen = OPEN_PATHS
                .stream()
                .anyMatch(path::startsWith);

        // GET /api/courses/** là public
        boolean isPublicCourseRead =
                path.startsWith("/api/courses")
                        && request.getMethod().name().equals("GET");

        // Nếu là public thì cho đi
        if (isOpen || isPublicCourseRead) {
            return chain.filter(exchange);
        }

        // Các request còn lại phải có Authorization
        if (!request.getHeaders().containsKey("Authorization")) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}