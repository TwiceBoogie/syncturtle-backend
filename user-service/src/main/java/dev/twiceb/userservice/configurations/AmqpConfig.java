package dev.twiceb.userservice.configurations;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.common.dto.response.UserPrincipleResponse;

import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AmqpConfig {

    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        jsonConverter.setClassMapper(classMapper());
        return jsonConverter;
    }

    private DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("emailRequest", EmailRequest.class);
        idClassMapping.put("userPrincipalResponse", UserPrincipleResponse.class);
        classMapper.setTrustedPackages("*");
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }
}
