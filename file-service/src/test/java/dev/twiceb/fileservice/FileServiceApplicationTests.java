package dev.twiceb.fileservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.amazonaws.services.s3.AmazonS3;

@SpringBootTest
class FileServiceApplicationTests {

    @MockitoBean
    AmazonS3 amazonS3;

    @Test
    void contextLoads() {
    }

}
