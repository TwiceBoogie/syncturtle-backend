package dev.twiceb.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

import static dev.twiceb.common.constants.FeignConstants.NOTIFICATION_SERVICE;
import static dev.twiceb.common.constants.PathConstants.API_V1_NOTIFICATION;

@FeignClient(name = NOTIFICATION_SERVICE, path = API_V1_NOTIFICATION, contextId = "NotificationClient")
public interface NotificationClient {


}
