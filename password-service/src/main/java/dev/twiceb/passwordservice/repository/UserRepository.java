package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.User;
import dev.twiceb.passwordservice.repository.projection.VaultHealthProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Long getUserByUsername(String username);

    @Query("SELECT user FROM User user WHERE user.id = :userId")
    VaultHealthProjection getUsersVaultHealth(@Param("userId") Long userId);
}
