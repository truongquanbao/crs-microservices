package vn.edu.crs.auth_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.crs.auth_service.dto.ApiKeyCreateRequestDTO;
import vn.edu.crs.auth_service.dto.ApiKeyResponseDTO;
import vn.edu.crs.auth_service.service.ApiKeyService;

import java.util.List;

@RestController
@RequestMapping("/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @GetMapping
    public List<ApiKeyResponseDTO> getAll() {
        return apiKeyService.getAll();
    }

    @PostMapping
    public ResponseEntity<ApiKeyResponseDTO> create(
            @Valid @RequestBody ApiKeyCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.create(request));
    }

    @DeleteMapping("/{id}")
    public ApiKeyResponseDTO revoke(@PathVariable Long id) {
        return apiKeyService.revoke(id);
    }
}
