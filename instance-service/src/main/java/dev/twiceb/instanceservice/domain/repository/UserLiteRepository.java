package dev.twiceb.instanceservice.domain.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.instanceservice.domain.model.UserLite;

public interface UserLiteRepository extends JpaRepository<UserLite, UUID> {

}
