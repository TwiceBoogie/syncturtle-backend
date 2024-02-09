package dev.twiceb.notificationservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notification_type", nullable = false)
    private String notificationType;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_read")
    private boolean isRead = false;

    @Column(name = "created_at")
    private Timestamp createdAt;
}
