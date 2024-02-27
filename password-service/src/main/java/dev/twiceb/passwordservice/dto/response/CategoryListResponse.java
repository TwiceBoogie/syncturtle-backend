package dev.twiceb.passwordservice.dto.response;

import lombok.Data;

@Data
public class CategoryListResponse {
    private Long id;
    private String name;
    private String description;
    private String color;
}
