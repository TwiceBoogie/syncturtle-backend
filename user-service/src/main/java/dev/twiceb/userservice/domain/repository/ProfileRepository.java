package dev.twiceb.userservice.domain.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.userservice.domain.model.Profile;
import dev.twiceb.userservice.domain.projection.ProfileProjection;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByUserId(UUID userId);

    Optional<ProfileProjection> findByUser_Id(UUID userId);
}
