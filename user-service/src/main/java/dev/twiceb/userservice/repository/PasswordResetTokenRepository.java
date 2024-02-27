package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findPasswordResetTokenByToken(String token);

}
