package dev.twiceb.workspace_service;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class PostgresTCBase {

    // closed by testcontainers
    @SuppressWarnings("resource")
    @Container
    static final PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine").withDatabaseName("user")
            .withUsername("test").withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);

        // so hibernate doesn't create schema; liquibase will
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }
}
