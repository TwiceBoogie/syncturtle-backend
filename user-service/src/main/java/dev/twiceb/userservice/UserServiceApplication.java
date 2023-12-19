package dev.twiceb.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = { "dev.twiceb" })
public class UserServiceApplication {

	// @Bean
	// public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
	// return new RabbitTemplate(connectionFactory);
	// }

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);

		// String[] beanNames = context.getBeanNamesForType(Object.class); // Get all
		// beans

		// // Iterate through the bean names and print AMQP-related beans
		// for (String beanName : beanNames) {
		// Object bean = context.getBean(beanName);
		// if (bean instanceof RabbitTemplate || bean instanceof ConnectionFactory) {
		// System.out.println("Bean Name: " + beanName + ", Bean Class: " +
		// bean.getClass());
		// // Print other relevant information about the bean
		// }
		// }
	}

}
