package dev.twiceb.apigateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import dev.twiceb.apigateway.dto.SessionRecord;
import dev.twiceb.apigateway.service.SessionService;
import dev.twiceb.apigateway.service.impl.SessionServiceImpl;

@Configuration
public class SessionServiceConfig {

    @Bean
    @Qualifier("userSession")
    SessionService userSession(ReactiveRedisTemplate<String, SessionRecord> redis) {
        return new SessionServiceImpl(redis, "sess:");
    }

    @Bean
    @Qualifier("adminSession")
    SessionService adminSession(ReactiveRedisTemplate<String, SessionRecord> redis) {
        return new SessionServiceImpl(redis, "admin:");
    }
}
