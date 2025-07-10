package dev.twiceb.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import jakarta.annotation.PostConstruct;

@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching
@SpringBootApplication(scanBasePackages = {"dev.twiceb.common", "dev.twiceb.apigateway"},
        exclude = {DataSourceAutoConfiguration.class})
public class ApiGatewayApplication {

    @Value("${security.frontend-api-key}")
    private String secureFetchKey;

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("Frontend API Key: " + secureFetchKey);
    }

    // @Bean
    // public CommandLineRunner printBeans(ApplicationContext context) {
    // return args -> {
    // String[] allBeanNames = context.getBeanDefinitionNames();
    // for (String beanName : allBeanNames) {
    // System.out.println("Bean Name: " + beanName);
    // }
    //
    // boolean authFilterExists = context.containsBean("authGatewayFilterFactory");
    // System.out.println("AuthFilter bean exists: " + authFilterExists);
    //
    // };
    // }

}
