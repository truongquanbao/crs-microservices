package vn.edu.crs.api_gateway.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyValidationCacheTests {

    @Test
    void cacheSeparatesScopesAndExpiresEntries() {
        MutableClock clock = new MutableClock();
        ApiKeyValidationCache cache = new ApiKeyValidationCache(30, clock);

        cache.put("key-1", "courses:read", true);
        cache.put("key-1", "courses:read-detail", false);

        assertThat(cache.get("key-1", "courses:read")).isTrue();
        assertThat(cache.get("key-1", "courses:read-detail")).isFalse();

        clock.advanceSeconds(30);
        assertThat(cache.get("key-1", "courses:read")).isNull();
    }

    private static final class MutableClock extends Clock {
        private Instant instant = Instant.parse("2026-09-03T00:00:00Z");

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }

        private void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }
    }
}
