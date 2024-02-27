package dev.twiceb.userservice.dto.response;

import lombok.Data;

@Data
public class ProfilePicResponse {
    private Long id;
    private String fileName;
    private String filePath;
}
