package dev.twiceb.taskservice.dto.response;

import lombok.Data;
import org.springframework.http.MediaType;

@Data
public class FileImageResponse {

    private byte[] photoContent;
    private MediaType fileType;
}
