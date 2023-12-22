package dev.twiceb.passwordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class PasswordsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PasswordsServiceApplication.class, args);
    }

}
