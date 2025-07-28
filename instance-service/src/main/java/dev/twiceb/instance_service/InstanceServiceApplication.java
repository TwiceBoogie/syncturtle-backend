package dev.twiceb.instance_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication(scanBasePackages = {"dev.twiceb.common", "dev.twiceb.instance_service"})
public class InstanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstanceServiceApplication.class, args);
    }

}
