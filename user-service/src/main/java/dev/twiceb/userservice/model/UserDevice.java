package dev.twiceb.userservice.model;

import dev.twiceb.userservice.service.util.DeviceKeyTransitConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_devices")
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "device_key", nullable = false)
    @Convert(converter = DeviceKeyTransitConverter.class)
    private String deviceKey;

    @Column(name = "first_access_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime firstAccessTimestamp = LocalDateTime.now();
}
