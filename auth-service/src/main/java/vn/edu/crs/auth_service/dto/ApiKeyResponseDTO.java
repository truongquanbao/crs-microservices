package vn.edu.crs.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import vn.edu.crs.auth_service.entity.ApiKeyStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiKeyResponseDTO {
    private Long id;
    private String keyValue;
    private String ownerName;
    private String scopes;
    private ApiKeyStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
