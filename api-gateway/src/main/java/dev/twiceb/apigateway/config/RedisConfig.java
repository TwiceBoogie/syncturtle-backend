package dev.twiceb.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.twiceb.apigateway.dto.SessionRecord;

// https://medium.com/@jerome.waibel/caching-with-spring-boot-and-redis-can-be-tricky-5f99548601b9
@Configuration
public class RedisConfig {

    @Bean
    ReactiveRedisTemplate<String, SessionRecord> redisOperations(
            ReactiveRedisConnectionFactory factory) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<SessionRecord> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, SessionRecord.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, SessionRecord> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, SessionRecord> context =
                builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

}
