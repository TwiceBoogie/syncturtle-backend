package dev.twiceb.userservice.client;

import dev.twiceb.common.dto.request.FileImageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static dev.twiceb.common.constants.FeignConstants.FILE_SERVICE;
import static dev.twiceb.common.constants.PathConstants.*;

@FeignClient(name = FILE_SERVICE, path = API_V1_FILE, contextId = "FileClient")
public interface FileClient {

    @PostMapping(UPLOAD_MULTIPLE)
    List<String> uploadImages(@RequestPart("files") MultipartFile[] files,
            @PathVariable String bucket);

    @PostMapping(GET_FILE_IMAGE)
    byte[] getFileImage(@RequestBody FileImageRequest request, @PathVariable String bucket);

    @DeleteMapping(DELETE_FILE_IMAGE)
    void deleteFileImage(@RequestBody FileImageRequest request, @PathVariable String bucket);
}
