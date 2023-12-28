package dev.twiceb.userservice.config;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.common.dto.response.UserPrincipleResponse;
import lombok.Getter;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Configuration
public class AmqpConfig {

    // @Value("${rabbitmq.exchanges.internal-fanout}")
    // private String fanoutExchange;

    // @Value("${rabbitmq.queues.internal-fanout-queue}")
    // private String fanoutQueue;

    // @Bean
    // FanoutExchange internalFanoutExchange() {
    // System.out.println(this.fanoutExchange);
    // return new FanoutExchange(this.fanoutExchange);
    // }

    // @Bean
    // Queue userCreatedQueue() {
    // System.out.println(this.fanoutQueue);
    // return new Queue(this.fanoutQueue, false);
    // }

    // @Bean
    // Binding internalToUserCreatedBinding() {
    // return BindingBuilder
    // .bind(userCreatedQueue())
    // .to(internalFanoutExchange());
    // }

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
