package vn.edu.crs.auth_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApiKeyCreateRequestDTO {

    @NotBlank(message = "Ten doi tac khong duoc de trong")
    private String ownerName;

    @NotBlank(message = "Scope khong duoc de trong")
    private String scopes;

    @Min(value = 1, message = "So ngay hieu luc phai lon hon 0")
    private Integer validDays;
}
