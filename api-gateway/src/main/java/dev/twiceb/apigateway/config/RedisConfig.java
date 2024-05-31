package dev.twiceb.apigateway.config;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import dev.twiceb.common.dto.response.UserPrincipleResponse;

// https://medium.com/@jerome.waibel/caching-with-spring-boot-and-redis-can-be-tricky-5f99548601b9
@Configuration
public class RedisConfig {

    @Bean
    LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    RedisCacheManager cacheManager(LettuceConnectionFactory redisConnectionFactory) {
        ObjectMapper myMapper = new ObjectMapper();
        myMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        myMapper.registerModule(new Hibernate6Module()
                .enable(Hibernate6Module.Feature.FORCE_LAZY_LOADING)
                .enable(Hibernate6Module.Feature.REPLACE_PERSISTENT_COLLECTIONS));
        myMapper.activateDefaultTyping(myMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY);
        Jackson2JsonRedisSerializer<UserPrincipleResponse> serializer = new Jackson2JsonRedisSerializer<>(myMapper,
                UserPrincipleResponse.class);

        RedisSerializationContext.SerializationPair<UserPrincipleResponse> serializationPair = RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(serializationPair)
                .entryTtl(Duration.ofHours(1)); // Set time-to-live for cache entries to 1 hr
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    @Bean
    KeyGenerator customKeyGenerator() {
        return (target, method, params) -> params[0];
    }

}
