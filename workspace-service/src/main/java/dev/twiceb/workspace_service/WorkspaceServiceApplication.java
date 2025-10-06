package dev.twiceb.workspace_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"dev.twiceb.common", "dev.twiceb.workspace_service"})
public class WorkspaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkspaceServiceApplication.class, args);
    }

}
