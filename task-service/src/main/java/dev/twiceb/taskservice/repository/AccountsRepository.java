package dev.twiceb.taskservice.repository;

import dev.twiceb.taskservice.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountsRepository extends JpaRepository<Accounts, Long> {

    @Query("SELECT acc FROM Accounts acc WHERE acc.userId = :userId AND acc.userStatus NOT IN ('BANNED', 'DELETED')")
    <T> Optional<T> findAccountByUserId(@Param("userId") Long userId, Class<T> clazz);
}
