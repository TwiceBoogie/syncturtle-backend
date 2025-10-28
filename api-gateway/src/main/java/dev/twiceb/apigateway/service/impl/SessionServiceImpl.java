package dev.twiceb.apigateway.service.impl;

import java.time.Duration;
import java.time.Instant;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import dev.twiceb.apigateway.dto.SessionRecord;
import dev.twiceb.apigateway.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class SessionServiceImpl implements SessionService {

    private final String PREFIX;
    private final ReactiveRedisTemplate<String, SessionRecord> redis;

    public SessionServiceImpl(ReactiveRedisTemplate<String, SessionRecord> redis, String prefix) {
        this.redis = redis;
        this.PREFIX = prefix;
    }

    @Override
    public Mono<Void> save(String sessionId, SessionRecord record) {
        Instant now = Instant.now();
        Duration ttl = Duration.between(now, record.getAbsoluteExpiresAt());
        if (ttl.isNegative() || ttl.isZero()) {
            return Mono
                    .error(new IllegalArgumentException("absoluteExpiresAt must be in the future"));
        }

        String key = key(sessionId);
        return redis.opsForValue().set(key, record).then(redis.expire(key, ttl)).then();
    }

    @Override
    public Mono<SessionRecord> find(String sessionId) {
        return redis.opsForValue().get(key(sessionId));
    }

    @Override
    public Mono<Void> extendExpiry(String sessionId, Instant newExpiresAt) {
        String key = key(sessionId);

        return redis.opsForValue().get(key).flatMap(current -> {
            if (current == null) {
                return Mono.empty();
            }

            // do not extend if revoked or expired
            Instant now = Instant.now();
            if (current.isRevoked() || current.getExpiresAt().isBefore(now)) {
                return Mono.empty(); // treat as unauthorized
            }

            if (!newExpiresAt.isAfter(current.getExpiresAt())) {
                return Mono.empty(); // nothing to od
            }

            SessionRecord updated = current.toBuilder().expiresAt(newExpiresAt).build();

            return redis.opsForValue().set(key, updated).then();
        }).then();
    }

    @Override
    public Mono<Void> delete(String sessionId) {
        return redis.delete(key(sessionId)).then();
    }

    private String key(String sessionId) {
        return PREFIX + sessionId;
    }

}
