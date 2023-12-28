package dev.twiceb.passwordservice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.common.dto.response.UserPrincipleResponse;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
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

    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();

        jsonConverter.setClassMapper(classMapper());
        return jsonConverter;
    }

    private DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("emailRequest", EmailRequest.class);
        idClassMapping.put("userPrincipleResponse", UserPrincipleResponse.class);
        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setTrustedPackages("*");
        return classMapper;
    }
}
