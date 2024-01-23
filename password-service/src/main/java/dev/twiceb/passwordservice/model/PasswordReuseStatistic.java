package dev.twiceb.passwordservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "password_reuse_statistics")
public class PasswordReuseStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Accounts account;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "reuse_count", nullable = false)
    private int reuseCount = 0;

    public PasswordReuseStatistic() {}

    public PasswordReuseStatistic(Accounts account, String passwordHash) {
        this.account = account;
        this.passwordHash = passwordHash;
    }
}
