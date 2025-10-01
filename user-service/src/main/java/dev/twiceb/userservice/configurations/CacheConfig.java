package dev.twiceb.userservice.configurations;

import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.twiceb.common.dto.response.ConfigResponse;
import dev.twiceb.common.dto.response.InstanceStatusResult;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper base) {
        ObjectMapper mapper = base.copy().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // dont fail if unknown props exist during deserialization
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // enable type info in JSON to support polmorphic deserialization
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS, JsonTypeInfo.As.PROPERTY);

        // defaults; string keys, generic json values, 1h ttl
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(mapper);

        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .entryTtl(Duration.ofHours(1)).disableCachingNullValues();

        // per-cache typed serializer for config map (preserves enum keys)
        Jackson2JsonRedisSerializer<ConfigResponse> cfgValueSerializer =
                new Jackson2JsonRedisSerializer<>(mapper, ConfigResponse.class);
        RedisCacheConfiguration cfgCache = defaults.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(cfgValueSerializer));

        // per-cache; typed serializer for status + shorter ttl
        Jackson2JsonRedisSerializer<InstanceStatusResult> statusValueSerializer =
                new Jackson2JsonRedisSerializer<>(mapper, InstanceStatusResult.class);
        RedisCacheConfiguration statusCache = defaults.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(statusValueSerializer))
                .entryTtl(Duration.ofSeconds(60));

        return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaults)
                .withCacheConfiguration("s2s:instance:configuration", cfgCache)
                .withCacheConfiguration("s2s:instance:status", statusCache).build();
    }

    @Bean
    KeyGenerator versionKeyGen() {
        return (target, method, params) -> "v:" + params[0];
    }
}
