package dev.twiceb.instanceservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.twiceb.instanceservice.dto.internal.CachedHttpResponse;

@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<String, CachedHttpResponse> redisTemplate(
            RedisConnectionFactory connectionFactory, ObjectMapper base) {
        ObjectMapper mapper = base.copy().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RedisTemplate<String, CachedHttpResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(
                new Jackson2JsonRedisSerializer<>(mapper, CachedHttpResponse.class));
        template.afterPropertiesSet();
        return template;
    }
}
