package dev.twiceb.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "user_devices")
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "device_key", nullable = false)
    private String deviceKey;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @OneToMany(mappedBy = "userDevice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserAction> userActionHistory = new ArrayList<>();

    @Column(name = "last_access", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime lastAccess = LocalDateTime.now();

    @Column(name = "first_access_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime firstAccessTimestamp = LocalDateTime.now();
}
