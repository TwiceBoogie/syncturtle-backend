package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.LockedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockUserRepository extends JpaRepository<LockedUser, Long> {
}
