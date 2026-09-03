package vn.edu.crs.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.crs.auth_service.entity.ApiKey;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyValue(String keyValue);
}
