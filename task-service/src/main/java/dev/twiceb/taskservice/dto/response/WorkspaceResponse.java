package dev.twiceb.taskservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class WorkspaceResponse {
    private UUID id;
    private String name;
}
