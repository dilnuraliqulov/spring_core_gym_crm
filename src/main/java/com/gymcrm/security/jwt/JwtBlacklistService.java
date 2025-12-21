package com.gymcrm.security.jwt;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtBlacklistService {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token, long expiryMillis) {
        blacklist.put(token, Instant.now().plusMillis(expiryMillis));
    }

    public boolean isBlacklisted(String token) {
        Instant expiry = blacklist.get(token);
        if (expiry == null) return false;
        if (Instant.now().isAfter(expiry)) {
            blacklist.remove(token); // cleanup expired
            return false;
        }
        return true;
    }
}
