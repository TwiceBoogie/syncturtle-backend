package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.PasswordReuseStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PasswordReuseStatisticRepository extends JpaRepository<PasswordReuseStatistic, UUID> {

    @Query("SELECT SUM(prs.reuseCount) FROM PasswordReuseStatistic prs WHERE prs.user.id = :userId")
    int getTotalReuseCount(@Param("userId") Long userId);

}
