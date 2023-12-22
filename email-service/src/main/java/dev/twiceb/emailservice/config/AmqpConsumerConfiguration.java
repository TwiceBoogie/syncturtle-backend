package dev.twiceb.emailservice.config;

import dev.twiceb.common.dto.request.EmailRequest;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Configuration
public class AmqpConsumerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AmqpConsumerConfiguration.class);

    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.mail}")
    private String mailQueue;

    @Value("${rabbitmq.routing-keys.internal-mail}")
    private String internalMailRoutingKey;

    @Bean
    public DirectExchange internalDirectExchange() {
        logger.info("Topic Exchange is created.");
        return new DirectExchange(this.internalExchange);
    }

    @Bean
    public Queue mailQueue() {
        logger.info("new queue is created");
        return new Queue(this.mailQueue);
    }

    @Bean
    public Binding internalToMailBinding() {
        logger.info("new bindingBuilder created");
        return BindingBuilder
                .bind(mailQueue())
                .to(internalDirectExchange())
                .with(this.internalMailRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        jsonConverter.setClassMapper(classMapper());
        return jsonConverter;
    }

    private DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("emailRequest", EmailRequest.class);
        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setTrustedPackages("dev.twiceb.common.dto.request");
        return classMapper;
    }
}
