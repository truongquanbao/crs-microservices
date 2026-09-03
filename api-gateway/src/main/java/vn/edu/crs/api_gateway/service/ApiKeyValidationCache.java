package vn.edu.crs.api_gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ApiKeyValidationCache {

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;
    private final Clock clock;

    @Autowired
    public ApiKeyValidationCache(
            @Value("${api-key.cache-ttl-seconds:30}") long ttlSeconds) {
        this(ttlSeconds, Clock.systemUTC());
    }

    ApiKeyValidationCache(long ttlSeconds, Clock clock) {
        this.ttlMillis = ttlSeconds * 1000;
        this.clock = clock;
    }

    public Boolean get(String apiKey, String scope) {
        String cacheKey = cacheKey(apiKey, scope);
        CacheEntry entry = cache.get(cacheKey);
        if (entry == null) {
            return null;
        }
        if (entry.expiresAtMillis() <= clock.millis()) {
            cache.remove(cacheKey, entry);
            return null;
        }
        return entry.valid();
    }

    public void put(String apiKey, String scope, boolean valid) {
        cache.put(cacheKey(apiKey, scope), new CacheEntry(valid, clock.millis() + ttlMillis));
    }

    private String cacheKey(String apiKey, String scope) {
        return apiKey + '\u0000' + scope;
    }

    private record CacheEntry(boolean valid, long expiresAtMillis) {
    }
}
