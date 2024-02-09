package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.LoginAttemptPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoginAttemptPolicyRepository extends JpaRepository<LoginAttemptPolicy, Long> {

}
