package dev.twiceb.userservice;

import dev.twiceb.common.event.UpdatePasswordChangeEvent;
import dev.twiceb.userservice.repository.projection.UserDeviceProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static dev.twiceb.common.constants.KafkaTopicConstants.UPDATE_PASSWORD_CHANGE_TOPIC;

//@Component
//@RequiredArgsConstructor
//public class UpdatePasswordChangeProducer {
//
//    KafkaTemplate<String, UpdatePasswordChangeEvent> kafkaTemplate;
//
//    public void sendUpdatePasswordChangeEvent(UserDeviceProjection userDeviceDetail, boolean success) {
//        kafkaTemplate.send(UPDATE_PASSWORD_CHANGE_TOPIC, toUpdatePasswordChangeEvent(userDeviceDetail, success));
//    }
//
//    private UpdatePasswordChangeEvent toUpdatePasswordChangeEvent(UserDeviceProjection userDeviceDetail, boolean success) {
//        String changeResult = success ? "success" : "failure";
//        return UpdatePasswordChangeEvent.builder()
//                .id(userDeviceDetail.getUserDeviceId())
//                .changeSuccess(success)
//                .changeResult(changeResult)
//                .build();
//    }
//}
