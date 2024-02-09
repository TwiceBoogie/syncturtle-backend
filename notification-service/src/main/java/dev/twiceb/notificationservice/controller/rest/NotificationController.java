package dev.twiceb.notificationservice.controller.rest;

import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.notificationservice.dto.response.NotificationResponse;
import dev.twiceb.notificationservice.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequestMapping(UI_V1_NOTIFICATION)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationMapper notificationMapper;

    @GetMapping(LIST)
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @RequestHeader(name = AUTH_USER_ID_HEADER) Long userId,
            @PageableDefault(size = 10) Pageable pageable
            ) {
        HeaderResponse<NotificationResponse> response = notificationMapper.getUserNotifications(userId, pageable);
        return ResponseEntity.ok().headers(response.getHeaders()).body(response.getItems());
    }

    @PatchMapping(UPDATE_READ_STATE)
    public ResponseEntity<Void> updateNotificationReadState(
            @RequestHeader(name = AUTH_USER_ID_HEADER) Long userId,
            @PathVariable("notificationId") Long notificationId
    ) {
        notificationMapper.updateNotificationReadState(userId, notificationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateAllNotificationReadState(@RequestHeader(name = AUTH_USER_ID_HEADER) Long userId) {
        notificationMapper.updateAllNotificationReadState(userId);
        return ResponseEntity.noContent().build();
    }
}
