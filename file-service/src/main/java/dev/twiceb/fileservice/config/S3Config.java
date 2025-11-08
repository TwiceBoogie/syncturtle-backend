package dev.twiceb.fileservice.config;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import dev.twiceb.fileservice.service.ObjectStorage;

@Configuration
@ConditionalOnProperty(prefix = "app.s3", name = "enabled", havingValue = "true")
public class S3Config {

    @Bean
    ObjectStorage s3ObjectStorage(AmazonS3 s3) {
        return new ObjectStorage() {

            @Override
            public String put(String bucket, String key, File file) {
                s3.putObject(new PutObjectRequest(bucket, key, file));
                return s3.getUrl(bucket, key).toString();
            }

            @Override
            public byte[] get(String bucket, String key) {
                try (S3ObjectInputStream inputStream = s3.getObject(bucket, key).getObjectContent()) {
                    return inputStream.readAllBytes();
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }

            @Override
            public void delete(String bucket, String key) {
                s3.deleteObject(bucket, key);
            }

        };
    }
}
