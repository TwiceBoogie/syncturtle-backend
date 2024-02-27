package dev.twiceb.taskservice.controller.rest;

import dev.twiceb.common.dto.request.NewRecurringEventRequest;
import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.taskservice.dto.request.NewSubTaskRequest;
import dev.twiceb.taskservice.dto.request.NewTaskRequest;
import dev.twiceb.taskservice.dto.request.UpdateTaskRequest;
import dev.twiceb.taskservice.dto.response.FileImageResponse;
import dev.twiceb.taskservice.mapper.TaskMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_TASK)
public class TaskController {

        private final TaskMapper taskMapper;

        @PostMapping
        public ResponseEntity<GenericResponse> createNewTask(
                        @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
                        @Valid @RequestBody NewTaskRequest request,
                        BindingResult bindingResult) {
                return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.createNewTask(
                                userId, request, bindingResult));
        }

        @PostMapping(UPLOAD_ATTACHMENTS)
        public ResponseEntity<GenericResponse> uploadAttachments(
                @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
                @RequestParam("files") MultipartFile[] files,
                @PathVariable("taskId") Long taskId
                ) {
                return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.uploadAttachments(
                        userId, taskId, files));
        }

        @GetMapping(GET_ATTACHMENT_FILE)
        public ResponseEntity<byte[]> getFileImage(
                @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
                @PathVariable("taskAttachmentId") Long taskAttachmentId
        ) {
                FileImageResponse res = taskMapper.getFileImage(userId, taskAttachmentId);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(res.getFileType());
                headers.setContentLength(res.getPhotoContent().length);

                return ResponseEntity.ok().headers(headers).body(res.getPhotoContent());
        }

        @PostMapping(CREATE_RECURRING_TASK)
        public ResponseEntity<GenericResponse> createRecurringTask(
                        @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
                        @Valid @RequestBody NewRecurringEventRequest request,
                        BindingResult bindingResult) {
                return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.createNewRecurringTask(
                                userId, request, bindingResult));
        }

        @PostMapping(CREATE_SUBTASK)
        public ResponseEntity<GenericResponse> addSubTaskToTask(
                        @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
                        @PathVariable("taskId") Long taskId,
                        @Valid @RequestBody NewSubTaskRequest request,
                        BindingResult bindingResult) {
                return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.addSubTaskToTask(
                                userId, taskId, request, bindingResult));
        }

        @PatchMapping
        public ResponseEntity<GenericResponse> updateTask(
                        @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
                        @Valid @RequestBody UpdateTaskRequest request,
                        BindingResult bindingResult) {
                return ResponseEntity.ok(taskMapper.updateTask(userId, request, bindingResult));
        }
}
