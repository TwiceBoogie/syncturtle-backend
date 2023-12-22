package dev.twiceb.passwordservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.twiceb.passwordservice.model.Accounts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountsRepository extends JpaRepository<Accounts, Long> {

    @Query("SELECT acc FROM Accounts acc WHERE acc.userId = :userId")
    <T> Optional<T> findAccountByUserId(@Param("userId") Long userId, Class<T> type);
}
