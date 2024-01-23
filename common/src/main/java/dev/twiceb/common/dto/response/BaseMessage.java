package dev.twiceb.common.dto.response;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class BaseMessage {

    private String messageType;

    public BaseMessage(String messageType) {
        this.messageType = messageType;
    }

}
