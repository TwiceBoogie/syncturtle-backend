package dev.twiceb.workspace_service.configuration;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.ExponentialBackOff;
import dev.twiceb.common.event.InstanceEvent;
import dev.twiceb.common.event.PlanEvent;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // trust
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "dev.twiceb.*");
        // producer has them off for cross service stability
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        // reprocess from beginning if new group
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    ProducerFactory<String, Object> dlProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    KafkaTemplate<String, Object> dltKafkaTemplate(ProducerFactory<String, Object> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, Object> dltTemplate) {
        ExponentialBackOff backoff = new ExponentialBackOff(500L, 2.0); // start 0.5s; double each
                                                                        // time
        backoff.setMaxElapsedTime(10_000L); // stop after 10s
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(dltTemplate,
                (rec, ex) -> new TopicPartition(rec.topic() + ".DLT", rec.partition()));
        return new DefaultErrorHandler(recoverer, backoff);
    }

    @Bean
    ConsumerFactory<String, InstanceEvent> instanceConsumerFactory() {
        Map<String, Object> props = baseProps();
        JsonDeserializer<InstanceEvent> value = new JsonDeserializer<>(InstanceEvent.class);
        value.addTrustedPackages("dev.twiceb.*");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), value);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, InstanceEvent> instanceKafkaFactory(
            ConsumerFactory<String, InstanceEvent> cf, DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, InstanceEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(3);
        return factory;
    }

    @Bean
    ConsumerFactory<String, PlanEvent> planConsumerFactory() {
        Map<String, Object> props = baseProps();
        JsonDeserializer<PlanEvent> value = new JsonDeserializer<>(PlanEvent.class);
        value.addTrustedPackages("dev.twiceb.*");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), value);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, PlanEvent> planKafkaFactory(
            ConsumerFactory<String, PlanEvent> cf, DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, PlanEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(3);
        return factory;
    }
}
