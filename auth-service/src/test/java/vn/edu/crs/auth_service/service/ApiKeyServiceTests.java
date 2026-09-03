package vn.edu.crs.auth_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.crs.auth_service.dto.ApiKeyCreateRequestDTO;
import vn.edu.crs.auth_service.dto.ApiKeyResponseDTO;
import vn.edu.crs.auth_service.entity.ApiKey;
import vn.edu.crs.auth_service.entity.ApiKeyStatus;
import vn.edu.crs.auth_service.repository.ApiKeyRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTests {

    @Mock
    private ApiKeyRepository repository;

    @Test
    void createReturnsSecretButListDoesNotExposeIt() {
        ApiKeyService service = new ApiKeyService(repository);
        ApiKeyCreateRequestDTO request = new ApiKeyCreateRequestDTO();
        request.setOwnerName("Partner A");
        request.setScopes("courses:read, courses:read");
        request.setValidDays(30);

        when(repository.findByKeyValue(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> {
            ApiKey saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ApiKeyResponseDTO created = service.create(request);
        assertThat(created.getKeyValue()).startsWith("crs_").hasSize(36);
        assertThat(created.getScopes()).isEqualTo("courses:read");
        assertThat(created.getStatus()).isEqualTo(ApiKeyStatus.ACTIVE);

        ApiKey persisted = new ApiKey(
                1L,
                created.getKeyValue(),
                created.getOwnerName(),
                created.getScopes(),
                created.getStatus(),
                created.getExpiresAt(),
                created.getCreatedAt()
        );
        when(repository.findAll()).thenReturn(List.of(persisted));
        List<ApiKeyResponseDTO> listed = service.getAll();
        assertThat(listed.get(0).getKeyValue()).isNull();
    }

    @Test
    void validationChecksScopeExpiryAndRevocation() {
        ApiKeyService service = new ApiKeyService(repository);
        ApiKey apiKey = new ApiKey(
                2L,
                "crs_test",
                "Partner B",
                "courses:read",
                ApiKeyStatus.ACTIVE,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now()
        );
        when(repository.findByKeyValue("crs_test")).thenReturn(Optional.of(apiKey));

        assertThat(service.isValidForScope("crs_test", "courses:read")).isTrue();
        assertThat(service.isValidForScope("crs_test", "courses:read-detail")).isFalse();

        apiKey.setExpiresAt(LocalDateTime.now().minusSeconds(1));
        assertThat(service.isValidForScope("crs_test", "courses:read")).isFalse();

        apiKey.setExpiresAt(LocalDateTime.now().plusDays(1));
        apiKey.setStatus(ApiKeyStatus.REVOKED);
        assertThat(service.isValidForScope("crs_test", "courses:read")).isFalse();
    }
}
