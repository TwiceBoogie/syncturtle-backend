package dev.twiceb.apigateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"dev.twiceb"},exclude = {DataSourceAutoConfiguration.class})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner printBeans(ApplicationContext context) {
//        return args -> {
//            String[] allBeanNames = context.getBeanDefinitionNames();
//            for (String beanName : allBeanNames) {
//                System.out.println("Bean Name: " + beanName);
//            }
//
//            boolean authFilterExists = context.containsBean("authGatewayFilterFactory");
//            System.out.println("AuthFilter bean exists: " + authFilterExists);
//
//        };
//    }

}
