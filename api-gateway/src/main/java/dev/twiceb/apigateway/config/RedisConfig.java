package dev.twiceb.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import dev.twiceb.common.dto.response.UserPrincipalResponse;

// https://medium.com/@jerome.waibel/caching-with-spring-boot-and-redis-can-be-tricky-5f99548601b9
@Configuration
public class RedisConfig {

    // @Bean
    // LettuceConnectionFactory redisConnectionFactory() {
    // return new LettuceConnectionFactory();
    // }

    // https://spring.io/guides/gs/spring-data-reactive-redis
    @Bean
    ReactiveRedisTemplate<String, UserPrincipalResponse> redisOperations(
            ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserPrincipalResponse> serializer =
                new Jackson2JsonRedisSerializer<>(UserPrincipalResponse.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, UserPrincipalResponse> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, UserPrincipalResponse> context =
                builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    // @Bean
    // RedisCacheManager cacheManager(LettuceConnectionFactory redisConnectionFactory) {
    // // create and config custom ObjectMapper
    // ObjectMapper myMapper = new ObjectMapper(); // serialize java objects to JSON and vice-versa

    // // Don't fail if unknown props exist during deserialization
    // myMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // // *** api-gateway should never know about hibernate internals
    // // Register Hibernate module to handle lazy-loaded proxies
    // // myMapper.registerModule(
    // // new Hibernate6Module().enable(Hibernate6Module.Feature.FORCE_LAZY_LOADING)
    // // .enable(Hibernate6Module.Feature.REPLACE_PERSISTENT_COLLECTIONS));

    // // enable type info in JSON to support polymorphic deserialization
    // myMapper.activateDefaultTyping(myMapper.getPolymorphicTypeValidator(),
    // ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS, // includes most types except final
    // // classes
    // JsonTypeInfo.As.PROPERTY // add @class property in the JSON
    // );

    // // create cust om serializer using the configured ObjectMapper
    // Jackson2JsonRedisSerializer<UserPrincipalResponse> serializer =
    // new Jackson2JsonRedisSerializer<>(myMapper, UserPrincipalResponse.class);

    // // create a redis serialization pair for chache values
    // RedisSerializationContext.SerializationPair<UserPrincipalResponse> serializationPair =
    // RedisSerializationContext.SerializationPair.fromSerializer(serializer);

    // // build cache config with: custom serializer, 1 hour expiration (TTL)
    // RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
    // .serializeValuesWith(serializationPair).entryTtl(Duration.ofHours(1));

    // return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(cacheConfig).build();
    // }

    // @Bean
    // KeyGenerator customKeyGenerator() {
    // return (target, method, params) -> params[0];
    // }

}
