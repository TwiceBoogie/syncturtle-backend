package dev.twiceb.instanceservice.configuration;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import dev.twiceb.common.event.InstanceEvent;
import dev.twiceb.common.event.PlanEvent;
import dev.twiceb.common.event.UserCreateEvent;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // dura
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // retry
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // for cross-service compability
        // don't add java type headers, keep json schema stable
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        return props;
    }

    @Bean
    DefaultKafkaProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    KafkaTemplate<String, InstanceEvent> instanceEventTemplate(
            DefaultKafkaProducerFactory<String, Object> pf) {
        return new KafkaTemplate(pf);
    }

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    KafkaTemplate<String, UserCreateEvent> userCreateTemplate(
            DefaultKafkaProducerFactory<String, Object> pf) {
        return new KafkaTemplate(pf);
    }

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    KafkaTemplate<String, PlanEvent> planEventTemplate(
            DefaultKafkaProducerFactory<String, Object> pf) {
        return new KafkaTemplate(pf);
    }

}
