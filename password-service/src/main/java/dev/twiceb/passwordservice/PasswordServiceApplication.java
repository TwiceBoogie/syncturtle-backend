package dev.twiceb.passwordservice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.beans.factory.annotation.Value;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = { "dev.twiceb" })
public class PasswordServiceApplication {

    // @Value("${spring.datasource.username}")
    // private String dbUsername;
    // @Value("${spring.datasource.password}")
    // private String dbPassword;

    public static void main(String[] args) {
        SpringApplication.run(PasswordServiceApplication.class, args);
    }

    // @PostConstruct
    // public void initIt() throws Exception {
    // System.out.println("Got DB user: " + dbUsername);
    // System.out.println("Got DB password: " + dbPassword);
    // }
}
