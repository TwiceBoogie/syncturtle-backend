package dev.twiceb.taskservice.repository;

import dev.twiceb.taskservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT acc FROM User acc WHERE acc.userId = :userId AND acc.userStatus = 'ACTIVE'")
    <T> Optional<T> findAccountByUserId(@Param("userId") UUID userId, Class<T> clazz);

    @Query("SELECT CASE WHEN count(acc) > 0 THEN true ELSE false END FROM User acc WHERE acc.userId = :userId")
    boolean isAccountExist(@Param("userId") UUID userId);
}
