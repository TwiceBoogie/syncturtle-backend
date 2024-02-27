package dev.twiceb.taskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = { "dev.twiceb.common", "dev.twiceb.taskservice" })
@EnableJpaRepositories(basePackages = { "dev.twiceb.common.repository", "dev.twiceb.taskservice.repository" })
@EntityScan(basePackages = { "dev.twiceb.common.model", "dev.twiceb.taskservice.model" })
public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }
}
