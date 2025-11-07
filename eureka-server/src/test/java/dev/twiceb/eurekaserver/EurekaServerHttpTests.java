package dev.twiceb.eurekaserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
public class EurekaServerHttpTests {

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
    void registryResponds() {
        ResponseEntity<String> res = rest.getForEntity("http://localhost:" + port + "/eureka/apps", String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        String body = res.getBody();
        assertNotNull(body);
        assertTrue(body.contains("applications"));
    }

}
