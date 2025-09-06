package dev.twiceb.userservice.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8000");
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Salvador Sebastian");
        myContact.setEmail("salsebastian13@gmail.com");

        Info information = new Info().title("User Service API").version("1.0")
                .description("This API exposes endpoints to manage users.").contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group("public-apis")
                .packagesToScan("dev.twiceb.userservice.controller.rest").pathsToMatch("/ui/**")
                .build();
    }
}
