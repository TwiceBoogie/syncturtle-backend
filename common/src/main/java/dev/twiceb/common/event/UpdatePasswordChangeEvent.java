package dev.twiceb.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordChangeEvent implements PasswordChangeLogEvent{
    private Long id;
    private boolean changeSuccess;
    private String changeResult;
    private Long userDeviceId;
}
