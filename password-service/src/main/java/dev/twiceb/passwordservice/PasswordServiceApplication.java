package dev.twiceb.passwordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = { "dev.twiceb.common", "dev.twiceb.passwordservice" })
public class PasswordServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PasswordServiceApplication.class, args);
    }

}
