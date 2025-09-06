package dev.twiceb.userservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.userservice.domain.model.PasswordResetToken;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    PasswordResetToken findPasswordResetTokenByToken(String token);

}
