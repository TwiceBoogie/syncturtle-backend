package dev.twiceb.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_profile_limit")
public class UserProfileLimt {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "profile_count")
    private int profileCount = 0;
}
