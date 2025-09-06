package dev.twiceb.userservice.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.userservice.domain.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByHandle(String handle);
}
