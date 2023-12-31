package dev.twiceb.taskservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_status", nullable = false)
    private String userStatus;

    public Accounts() {}

    public Accounts(Long userId, String userStatus) {
        this.userId = userId;
        this.userStatus = userStatus;
    }
}
