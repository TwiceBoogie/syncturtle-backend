package dev.twiceb.apigateway.service;

import java.time.Instant;
import dev.twiceb.apigateway.dto.SessionRecord;
import reactor.core.publisher.Mono;

public interface SessionService {
    Mono<Void> save(String sessionId, SessionRecord record);

    Mono<SessionRecord> find(String sessionId);

    Mono<Void> extendExpiry(String sessionId, Instant newExpiresAt);

    Mono<Void> delete(String sessionId);
}
