package dev.twiceb.fileservice.controller;

import dev.twiceb.common.dto.request.FileImageRequest;
import dev.twiceb.fileservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_FILE)
public class FileController {

    private final FileService fileService;

    @PostMapping(UPLOAD)
    public String uploadImage(@RequestPart("file") MultipartFile file, @PathVariable String bucket) {
        return fileService.uploadImage(file, bucket);
    }

    @PostMapping(UPLOAD_MULTIPLE)
    public List<String> uploadImages(@RequestPart("files") MultipartFile[] files, @PathVariable String bucket) {
        return fileService.uploadImages(files, bucket);
    }

    @PostMapping(GET_FILE_IMAGE)
    public byte[] getFileImage(@RequestBody FileImageRequest request, @PathVariable String bucket) {
        return fileService.getFileImage(request, bucket);
    }

    @DeleteMapping(DELETE_FILE_IMAGE)
    public void deleteFileImage(@RequestBody FileImageRequest request, @PathVariable String bucket) {
        fileService.deleteFileImage(request, bucket);
    }
}
