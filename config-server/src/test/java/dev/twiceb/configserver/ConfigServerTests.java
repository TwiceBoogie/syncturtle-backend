package dev.twiceb.configserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigServerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void healthIsUp() {
        ResponseEntity<Map<String, Object>> res = rest.exchange("http://localhost:" + port + "/actuator/health",
                HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
                });

        assertEquals(res.getStatusCode(), HttpStatus.OK);
        Map<String, Object> body = res.getBody();
        assertNotNull(body);
        assertEquals("UP", body.get("status"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void servesConfigFromNative() {
        ResponseEntity<Map<String, Object>> res = rest.exchange("http://localhost:" + port + "/test-app/dev",
                HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
                });
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        Map<String, Object> env = res.getBody();
        assertNotNull(env);

        Object ps = env.get("propertySources");
        assertTrue(ps instanceof List);

        boolean found = false;
        for (Object o : (List<?>) ps) {
            if (!(o instanceof Map))
                continue;
            Map<String, Object> entry = (Map<String, Object>) o;
            Object src = entry.get("source");
            if (!(src instanceof Map))
                continue;
            Map<String, Object> source = (Map<String, Object>) src;

            Object greetingMessage = source.get("greeting.message");
            if ("hello-from-test".equals(greetingMessage)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Expected greeting.message=hello-from-test in at least one property source");
    }
}
