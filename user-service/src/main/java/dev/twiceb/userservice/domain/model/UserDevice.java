package dev.twiceb.userservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;
import dev.twiceb.userservice.utils.TokenGenerator;

@Entity
@Getter
@Table(name = "user_devices")
public class UserDevice {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    // UserDevice -> User (many devices per user)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_key", nullable = false)
    private String deviceKey;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "last_access", nullable = false)
    private Instant lastAccess;

    @Column(name = "first_access_timestamp", nullable = false, updatable = false)
    private Instant firstAccessTimestamp;

    protected UserDevice() {} // jpa-friendly and not public

    public static UserDevice create(String deviceName) {
        UserDevice d = new UserDevice();
        d.deviceName = (deviceName == null || deviceName.isBlank())
                ? "register_device_" + UUID.randomUUID().toString().replace("-", "")
                : deviceName.trim().toLowerCase().replace(" ", "_");
        d.deviceKey = "dk_" + TokenGenerator.randomUrlToken256();

        return d;
    }

    public boolean isNotifyUserEnabled() {
        return this.user.isNotifyPasswordChange();
    }

}
