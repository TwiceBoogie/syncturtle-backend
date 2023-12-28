package dev.twiceb.common.dto.response;

public abstract class BaseMessage {

    private String messageType;

    public BaseMessage(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }
}
