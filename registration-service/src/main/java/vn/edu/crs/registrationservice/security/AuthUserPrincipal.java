package vn.edu.crs.registrationservice.security;

public record AuthUserPrincipal(Long userId, String username, String role) {
}
