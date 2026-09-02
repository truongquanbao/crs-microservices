package vn.edu.crs.registrationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    @Value("${jwt.secret:CRS-Microservices-Secret-Key-Nam-3-Hoc-Ky-2026-Doi-Trong-Thuc-Te}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");
        if (userId == null) {
            throw new IllegalArgumentException("JWT missing userId claim");
        }
        return Long.valueOf(userId.toString());
    }

    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role == null ? null : role.toString();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject() != null
                    && claims.get("role") != null
                    && claims.get("userId") != null
                    && claims.getExpiration() != null
                    && !claims.getExpiration().before(new java.util.Date());
        } catch (Exception ex) {
            return false;
        }
    }
}
