package dev.twiceb.passwordservice.feign;

import dev.twiceb.common.dto.request.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static dev.twiceb.common.constants.FeignConstants.NOTIFICATION_SERVICE;
import static dev.twiceb.common.constants.PathConstants.API_V1_NOTIFICATION;
import static dev.twiceb.common.constants.PathConstants.BATCH_NOTIFICATION;

@FeignClient(name = NOTIFICATION_SERVICE, path = API_V1_NOTIFICATION, contextId = "NotificationClient")
public interface NotificationClient {

    @GetMapping
    void sendNotification(@RequestBody NotificationRequest request);

    @GetMapping(BATCH_NOTIFICATION)
    void sendBatchNotification(@RequestBody List<NotificationRequest> request);
}
