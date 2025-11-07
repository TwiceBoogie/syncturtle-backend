package dev.twiceb.passwordservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import dev.twiceb.passwordservice.model.support.CryptoPort;

@SpringBootTest
class PasswordsServiceApplicationTests extends PostgresTCBase {

    @MockitoBean
    CryptoPort crypto;

    @Test
    void contextLoads() {
    }

}
