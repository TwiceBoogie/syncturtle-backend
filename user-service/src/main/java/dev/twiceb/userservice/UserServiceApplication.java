package dev.twiceb.userservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.vault.core.lease.SecretLeaseContainer;

@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisRepositories
@SpringBootApplication(scanBasePackages = { "dev.twiceb.common", "dev.twiceb.userservice" })
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner printBeans(ApplicationContext context) {
//		return args -> {
//			if (context.containsBean("secretLeaseContainer")) {
//				SecretLeaseContainer leaseContainer = context.getBean(SecretLeaseContainer.class);
//				System.out.println(leaseContainer);
//				// Now you can work with the SecretLeaseContainer instance
//				System.out.println("SecretLeaseContainer bean exists!");
//				// Do whatever you need with leaseContainer
//			} else {
//				System.out.println("SecretLeaseContainer bean does not exist!");
//				// Create a new instance of SecretLeaseContainer manually if needed
//				// SecretLeaseContainer leaseContainer = new SecretLeaseContainer();
//			}
//		};
//	}
}
