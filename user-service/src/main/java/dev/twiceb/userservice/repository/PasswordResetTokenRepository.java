package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    PasswordResetToken findPasswordResetTokenByToken(String token);

}
