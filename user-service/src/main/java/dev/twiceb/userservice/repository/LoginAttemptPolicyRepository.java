package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.LoginAttemptPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAttemptPolicyRepository extends JpaRepository<LoginAttemptPolicy, Long> {

}
