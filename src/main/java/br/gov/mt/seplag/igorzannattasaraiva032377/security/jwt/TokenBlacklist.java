package br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * In-memory blacklist para access/refresh tokens. Não persiste em banco.
 */
@Component
public class TokenBlacklist {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token, long expiresAtMillis) {
        blacklist.put(token, expiresAtMillis);
        cleanup();
    }

    public boolean isBlacklisted(String token) {
        var exp = blacklist.get(token);
        if (exp == null) {
            return false;
        }
        if (exp < Instant.now().toEpochMilli()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    private void cleanup() {
        var now = Instant.now().toEpochMilli();
        blacklist.entrySet().removeIf(e -> e.getValue() < now);
    }
}
