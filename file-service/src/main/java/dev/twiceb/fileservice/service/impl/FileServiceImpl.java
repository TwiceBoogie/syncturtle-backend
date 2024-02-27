package dev.twiceb.fileservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import dev.twiceb.common.dto.request.FileImageRequest;
import dev.twiceb.fileservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final String TASK_ATTACHMENT_FOLDER = "TASK";
    private static final String USER_PROFILE_FOLDER = "USER";

    private final AmazonS3 amazonS3Client;

    @Value("${amazon.s3.bucket.task.name}")
    private String taskBucketName;
    @Value("${amazon.s3.bucket.user.name}")
    private String userBucketName;

    @Override
    public String uploadImage(MultipartFile multipartFile, String bucket) {
        String image = null;
        String bucketName = getBucketName(bucket);
        if (multipartFile != null) {
            File file = new File(multipartFile.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
            image = amazonS3Client.getUrl(bucketName, fileName).toString();
            file.delete();
        }
        return image;
    }

    @Override
    public List<String> uploadImages(MultipartFile[] multipartFiles, String bucket) {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String imageUrl = uploadImage(multipartFile, bucket);
            if (imageUrl != null) {
                imageUrls.add(imageUrl);
            }
        }
        return imageUrls;
    }

    @Override
    public byte[] getFileImage(FileImageRequest request, String bucket) {
        String bucketName = getBucketName(bucket);
        S3Object photoObject = amazonS3Client.getObject(bucketName, extractKeyFromImageUrl(request.getImageUrl()));
        byte[] photoBytes = null;
        try (S3ObjectInputStream inputStream = photoObject.getObjectContent()) {
            photoBytes = inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photoBytes;
    }

    @Override
    public void deleteFileImage(FileImageRequest request, String bucket) {
        String bucketName = getBucketName(bucket);
        amazonS3Client.deleteObject(bucketName, extractKeyFromImageUrl(request.getImageUrl()));
    }

    private String getBucketName(String bucket) {
        String bucketName = null;
        if (bucket.equals(TASK_ATTACHMENT_FOLDER)) {
            bucketName = taskBucketName;
        } else if (bucket.equals(USER_PROFILE_FOLDER)) {
            bucketName = userBucketName;
        }
        return bucketName;
    }

    private String extractKeyFromImageUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        return parts[parts.length - 1];
    }
}
