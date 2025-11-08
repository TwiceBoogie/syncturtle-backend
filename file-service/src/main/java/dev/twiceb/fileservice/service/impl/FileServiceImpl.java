package dev.twiceb.fileservice.service.impl;

import dev.twiceb.common.dto.request.FileImageRequest;
import dev.twiceb.fileservice.service.FileService;
import dev.twiceb.fileservice.service.ObjectStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final String TASK_ATTACHMENT_FOLDER = "TASK";
    private static final String USER_PROFILE_FOLDER = "USER";

    private final ObjectStorage storage;

    @Value("${amazon.s3.bucket.task.name}")
    private String taskBucketName;
    @Value("${amazon.s3.bucket.user.name}")
    private String userBucketName;

    @Override
    public String uploadImage(MultipartFile multipartFile, String bucket) {
        if (multipartFile == null) {
            return null;
        }

        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        String bucketName = getBucketName(bucket);

        try {
            File tmp = File.createTempFile("upload-", "-" + multipartFile.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(tmp)) {
                fos.write(multipartFile.getBytes());
            }
            String url = storage.put(bucketName, fileName, tmp);
            tmp.delete();
            return url;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        return storage.get(getBucketName(bucket), extractKeyFromImageUrl(request.getImageUrl()));
    }

    @Override
    public void deleteFileImage(FileImageRequest request, String bucket) {
        storage.delete(getBucketName(bucket), extractKeyFromImageUrl(request.getImageUrl()));
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
