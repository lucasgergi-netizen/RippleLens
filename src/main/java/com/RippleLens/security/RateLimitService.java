package com.RippleLens.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private static final int MAX_BUCKETS = 20_000;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();
    private final Object bucketCreationLock = new Object();
    private final WindowCounter capacityGuard = new WindowCounter(Instant.EPOCH);

    public void check(String policy, String subject, int maximumAttempts, Duration window) {
        Instant now = Instant.now();
        String key = policy + ':' + subject;
        WindowCounter counter = counters.get(key);
        if (counter == null) {
            synchronized (bucketCreationLock) {
                counter = counters.get(key);
                if (counter == null) {
                    if (counters.size() >= MAX_BUCKETS) {
                        counters.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
                    }
                    if (counters.size() >= MAX_BUCKETS) {
                        counter = capacityGuard;
                    } else {
                        counter = new WindowCounter(now.plus(window));
                        counters.put(key, counter);
                    }
                }
            }
        }
        long retryAfter = counter.tryAcquire(now, maximumAttempts, window);
        if (retryAfter > 0) {
            throw new RateLimitExceededException(retryAfter);
        }
    }

    void clearForTests() {
        synchronized (bucketCreationLock) {
            counters.clear();
            capacityGuard.reset();
        }
    }

    int bucketCountForTests() {
        return counters.size();
    }

    private static final class WindowCounter {
        private Instant resetAt;
        private int attempts;

        private WindowCounter(Instant resetAt) {
            this.resetAt = resetAt;
        }

        private synchronized long tryAcquire(Instant now, int maximumAttempts, Duration window) {
            if (!now.isBefore(resetAt)) {
                resetAt = now.plus(window);
                attempts = 0;
            }
            if (attempts >= maximumAttempts) {
                return Math.max(1, Duration.between(now, resetAt).toSeconds());
            }
            attempts++;
            return 0;
        }

        private synchronized boolean isExpired(Instant now) {
            return !now.isBefore(resetAt);
        }

        private synchronized void reset() {
            resetAt = Instant.EPOCH;
            attempts = 0;
        }
    }
}
