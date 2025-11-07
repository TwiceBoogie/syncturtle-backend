package dev.twiceb.passwordservice.amqp;

import dev.twiceb.common.dto.response.UserPrincipalResponse;

public interface MessagePublisher {
    void userCreatedListener(UserPrincipalResponse res);
}
