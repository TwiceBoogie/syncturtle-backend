package dev.twiceb.userservice.model;

import dev.twiceb.common.enums.TimePeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "user_statistics")
public class UserStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(read = "time_period::text", write = "?::time_period")
    private TimePeriod intervalType;

    @Column(name = "active_user_count", nullable = false)
    private int activeUserCount = 0;

    @Column(name = "registered_users", nullable = false)
    private int registeredUsers = 0;

    @Column(name = "registered_users_change", columnDefinition = "DECIMAL(5,2)")
    private double registeredUsersChange;

    @Column(name = "created_date", updatable = false)
    private Instant createdAt;
}
