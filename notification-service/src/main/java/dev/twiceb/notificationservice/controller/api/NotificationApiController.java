package dev.twiceb.notificationservice.controller.api;

import dev.twiceb.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.twiceb.common.constants.PathConstants.API_V1_NOTIFICATION;

@RestController
@RequestMapping(API_V1_NOTIFICATION)
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;
}
