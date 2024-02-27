package dev.twiceb.fileservice.service;

import dev.twiceb.common.dto.request.FileImageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    String uploadImage(MultipartFile multipartFile, String bucket);

    List<String> uploadImages(MultipartFile[] multipartFiles, String bucket);

    byte[] getFileImage(FileImageRequest request, String bucket);

    void deleteFileImage(FileImageRequest request, String bucket);
}
