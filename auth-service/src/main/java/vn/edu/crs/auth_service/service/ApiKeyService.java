package vn.edu.crs.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.crs.auth_service.dto.ApiKeyCreateRequestDTO;
import vn.edu.crs.auth_service.dto.ApiKeyResponseDTO;
import vn.edu.crs.auth_service.entity.ApiKey;
import vn.edu.crs.auth_service.entity.ApiKeyStatus;
import vn.edu.crs.auth_service.repository.ApiKeyRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private static final Set<String> ALLOWED_SCOPES = Set.of(
            "courses:read",
            "courses:read-detail"
    );
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final ApiKeyRepository apiKeyRepository;

    @Transactional
    public ApiKeyResponseDTO create(ApiKeyCreateRequestDTO request) {
        ApiKey apiKey = new ApiKey();
        apiKey.setKeyValue(generateUniqueKey());
        apiKey.setOwnerName(request.getOwnerName().trim());
        apiKey.setScopes(normalizeScopes(request.getScopes()));
        apiKey.setStatus(ApiKeyStatus.ACTIVE);
        apiKey.setCreatedAt(LocalDateTime.now());
        if (request.getValidDays() != null) {
            apiKey.setExpiresAt(apiKey.getCreatedAt().plusDays(request.getValidDays()));
        }

        return toResponse(apiKeyRepository.save(apiKey), true);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyResponseDTO> getAll() {
        return apiKeyRepository.findAll().stream()
                .map(apiKey -> toResponse(apiKey, false))
                .toList();
    }

    @Transactional
    public ApiKeyResponseDTO revoke(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay API key"));
        apiKey.setStatus(ApiKeyStatus.REVOKED);
        return toResponse(apiKeyRepository.save(apiKey), false);
    }

    @Transactional(readOnly = true)
    public boolean isValidForScope(String keyValue, String requiredScope) {
        if (keyValue == null || keyValue.isBlank() || requiredScope == null) {
            return false;
        }

        return apiKeyRepository.findByKeyValue(keyValue)
                .filter(apiKey -> apiKey.getStatus() == ApiKeyStatus.ACTIVE)
                .filter(apiKey -> apiKey.getExpiresAt() == null
                        || apiKey.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(ApiKey::getScopes)
                .map(this::splitScopes)
                .map(scopes -> scopes.contains(requiredScope))
                .orElse(false);
    }

    private String generateUniqueKey() {
        String candidate;
        do {
            byte[] randomBytes = new byte[24];
            SECURE_RANDOM.nextBytes(randomBytes);
            candidate = "crs_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        } while (apiKeyRepository.findByKeyValue(candidate).isPresent());
        return candidate;
    }

    private String normalizeScopes(String rawScopes) {
        Set<String> scopes = splitScopes(rawScopes);
        if (scopes.isEmpty() || !ALLOWED_SCOPES.containsAll(scopes)) {
            throw new IllegalArgumentException(
                    "Scope chi duoc phep: " + String.join(", ", ALLOWED_SCOPES)
            );
        }
        return String.join(",", scopes);
    }

    private Set<String> splitScopes(String rawScopes) {
        if (rawScopes == null) {
            return Set.of();
        }
        return Arrays.stream(rawScopes.split(","))
                .map(String::trim)
                .filter(scope -> !scope.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private ApiKeyResponseDTO toResponse(ApiKey apiKey, boolean includeSecret) {
        return new ApiKeyResponseDTO(
                apiKey.getId(),
                includeSecret ? apiKey.getKeyValue() : null,
                apiKey.getOwnerName(),
                apiKey.getScopes(),
                apiKey.getStatus(),
                apiKey.getExpiresAt(),
                apiKey.getCreatedAt()
        );
    }
}
