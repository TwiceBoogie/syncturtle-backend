package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.PasswordReuseStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PasswordReuseStatisticRepository extends JpaRepository<PasswordReuseStatistic, Long> {

    @Query("SELECT prs FROM PasswordReuseStatistic prs WHERE prs.account.id = :accountId")
    List<PasswordReuseStatistic> findAllByAccountId(@Param("accountId") Long accountId);
}
