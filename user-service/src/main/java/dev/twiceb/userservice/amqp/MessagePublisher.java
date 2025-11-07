package dev.twiceb.userservice.amqp;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.userservice.domain.projection.UserPrincipalProjection;

public interface MessagePublisher {
    void userCreated(UserPrincipalProjection user);

    void sendEmail(EmailRequest emailRequest);
}
