package vn.edu.crs.api_gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient(
            WebClient.Builder webClientBuilder,
            @Value("${auth-service.base-url:http://localhost:8081}") String authServiceBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceBaseUrl).build();
    }

    public Mono<Boolean> validateApiKey(String apiKey, String scope) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/api-keys/validate")
                        .queryParam("key", apiKey)
                        .queryParam("scope", scope)
                        .build())
                .retrieve()
                .bodyToMono(ValidationResponse.class)
                .map(ValidationResponse::valid)
                .defaultIfEmpty(false)
                .timeout(Duration.ofSeconds(2))
                .onErrorReturn(false);
    }

    private record ValidationResponse(boolean valid) {
    }
}
