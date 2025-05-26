package dev.twiceb.common.event;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordChangeEvent implements PasswordChangeLogEvent {
    private UUID id;
    private boolean changeSuccess;
    private String changeResult;
    private UUID userDeviceId;

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public boolean isChangeSuccess() {
        return this.changeSuccess;
    }

    @Override
    public String getChangeResult() {
        return this.changeResult;
    }

    @Override
    public UUID getUserDeviceId() {
        return this.userDeviceId;
    }
}
