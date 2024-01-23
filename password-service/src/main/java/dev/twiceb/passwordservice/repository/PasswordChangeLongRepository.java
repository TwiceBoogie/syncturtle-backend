package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.PasswordChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordChangeLongRepository extends JpaRepository<PasswordChangeLog, Long> {
}
