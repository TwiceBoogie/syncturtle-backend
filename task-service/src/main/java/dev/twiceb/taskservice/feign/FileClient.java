package dev.twiceb.taskservice.feign;

import dev.twiceb.common.dto.request.FileImageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static dev.twiceb.common.constants.FeignConstants.FILE_SERVICE;
import static dev.twiceb.common.constants.PathConstants.API_V1_FILE;
import static dev.twiceb.common.constants.PathConstants.UPLOAD_MULTIPLE;

@FeignClient(name = FILE_SERVICE, path = API_V1_FILE, contextId = "FileClient")
public interface FileClient {

    @PostMapping(UPLOAD_MULTIPLE)
    List<String> uploadImages(@RequestPart("files") MultipartFile[] files, @PathVariable String folder);

    @PostMapping
    byte[] getFileImage(@RequestBody FileImageRequest request);
}
