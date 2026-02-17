package com.gymcrm.security.brute;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BruteForceProtectionService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_MINUTES = 15;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();

    public boolean isBlocked(String username) {
        LocalDateTime blockTime = blockedUntil.get(username);
        if (blockTime == null) return false;

        if (LocalDateTime.now().isAfter(blockTime)) {
            blockedUntil.remove(username);
            attempts.remove(username);
            return false;
        }
        return true;
    }

    public void loginFailed(String username) {
        int attempt = attempts.getOrDefault(username, 0) + 1;
        attempts.put(username, attempt);

        if (attempt >= MAX_ATTEMPTS) {
            blockedUntil.put(username, LocalDateTime.now().plusMinutes(BLOCK_MINUTES));
        }
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
        blockedUntil.remove(username);
    }
}
