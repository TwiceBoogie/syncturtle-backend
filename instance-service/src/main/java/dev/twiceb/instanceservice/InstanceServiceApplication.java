package dev.twiceb.instanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import dev.twiceb.instanceservice.service.util.AppProperties;

@EnableJpaAuditing
@EnableFeignClients
@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication(scanBasePackages = {"dev.twiceb.common", "dev.twiceb.instanceservice"})
public class InstanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstanceServiceApplication.class, args);
    }

}
