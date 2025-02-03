package org.sysc4907.votingsystem.Authentication;

import io.github.bucket4j.Bucket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Duration;

public class RateLimiter {
    private static final int TOKENS_PER_MINUTE = 3;
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() { // 3 tokens per minute
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(TOKENS_PER_MINUTE).refillIntervally(TOKENS_PER_MINUTE, REFILL_DURATION))
                .build();
    }

    public boolean tryConsume(String userId) {
        Bucket bucket = buckets.computeIfAbsent(userId, k -> createNewBucket());
        return bucket.tryConsume(1);
    }

    public long getAvailableTokens(String userId) {
        return buckets.getOrDefault(userId, createNewBucket()).getAvailableTokens();
    }

    public void resetBucket(String userId) {
        buckets.put(userId, createNewBucket());
    }
}
