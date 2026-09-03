package vn.edu.crs.api_gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import vn.edu.crs.api_gateway.service.ApiKeyValidationCache;
import vn.edu.crs.api_gateway.service.AuthServiceClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyFilterTests {

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private GatewayFilterChain chain;

    private ApiKeyFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ApiKeyFilter(authServiceClient, new ApiKeyValidationCache(30));
    }

    @Test
    void missingKeyIsForbidden() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/public/courses").build()
        );

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verifyNoInteractions(authServiceClient, chain);
    }

    @Test
    void successfulValidationIsCachedPerScope() {
        MockServerWebExchange first = exchange("/api/public/courses", "key-1");
        MockServerWebExchange second = exchange("/api/public/courses", "key-1");
        when(authServiceClient.validateApiKey("key-1", "courses:read")).thenReturn(Mono.just(true));
        when(chain.filter(first)).thenReturn(Mono.empty());
        when(chain.filter(second)).thenReturn(Mono.empty());

        filter.filter(first, chain).block();
        filter.filter(second, chain).block();

        verify(authServiceClient, times(1)).validateApiKey("key-1", "courses:read");
        verify(chain, times(2)).filter(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void detailRequiresItsOwnScope() {
        MockServerWebExchange list = exchange("/api/public/courses", "key-2");
        MockServerWebExchange detail = exchange("/api/public/courses/1", "key-2");
        when(authServiceClient.validateApiKey("key-2", "courses:read")).thenReturn(Mono.just(true));
        when(authServiceClient.validateApiKey("key-2", "courses:read-detail")).thenReturn(Mono.just(false));
        when(chain.filter(list)).thenReturn(Mono.empty());

        filter.filter(list, chain).block();
        filter.filter(detail, chain).block();

        assertThat(detail.getResponse().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(authServiceClient).validateApiKey("key-2", "courses:read");
        verify(authServiceClient).validateApiKey("key-2", "courses:read-detail");
    }

    private MockServerWebExchange exchange(String path, String apiKey) {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get(path).header("X-API-KEY", apiKey).build()
        );
    }
}
