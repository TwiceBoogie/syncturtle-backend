package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.User;
import dev.twiceb.passwordservice.repository.projection.VaultHealthProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Long getUserByEmail(String email);

    @Query("SELECT user FROM User user WHERE user.id = :userId")
    VaultHealthProjection getUsersVaultHealth(@Param("userId") UUID userId);
}
