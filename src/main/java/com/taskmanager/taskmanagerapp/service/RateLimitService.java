package com.taskmanager.taskmanagerapp.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {
    Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Value("${rate.limit.capacity:100}")
    private int capacity;

    @Value("${rate.limit.refill.period:1}")
    private int refillPeriodMinutes;

    @Value("${rate.limit.refill.tokens:100}")
    private int refillTokens;

    public boolean tryConsume(String identifier) {
        Bucket bucket = buckets.computeIfAbsent(identifier, k -> createBucket());
        boolean consumed = bucket.tryConsume(1);

        if (!consumed) {
            log.warn("Rate limit exceeded for identifier: {}", identifier);
        }

        return consumed;
    }

    private Bucket createBucket() {
        // Bandwidth = rate limiting policy
        Bandwidth limit = Bandwidth.classic(
                capacity,  // Initial capacity: 100 tokens
                Refill.intervally(
                        refillTokens,  // Add 100 tokens
                        Duration.ofMinutes(refillPeriodMinutes)  // Every 1 minute
                )
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public long getAvailableTokens(String identifier) {
        Bucket bucket = buckets.get(identifier);
        return bucket != null ? bucket.getAvailableTokens() : capacity;
    }

    public void resetLimit(String identifier) {
        buckets.remove(identifier);
        log.info("Rate limit reset for identifier: {}", identifier);
    }

    public void clearAll() {
        buckets.clear();
        log.info("All rate limit buckets cleared");
    }
}
