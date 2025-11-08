package dev.twiceb.fileservice.config;

import java.io.File;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;

import dev.twiceb.fileservice.service.ObjectStorage;

@Configuration
@ConditionalOnMissingBean(AmazonS3.class)
public class NoopStorageConfig {

    @Bean
    ObjectStorage noopStorage() {
        return new ObjectStorage() {

            @Override
            public String put(String bucket, String key, File file) {
                return "memory://" + "/" + key;
            }

            @Override
            public byte[] get(String bucket, String key) {
                return new byte[0];
            }

            @Override
            public void delete(String bucket, String key) {

            }

        };
    }
}
