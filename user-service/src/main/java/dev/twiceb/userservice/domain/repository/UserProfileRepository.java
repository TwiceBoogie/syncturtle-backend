package dev.twiceb.userservice.domain.repository;

import dev.twiceb.userservice.domain.model.UserProfile;
import dev.twiceb.userservice.domain.projection.ProfilePicUrlProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query("SELECT up FROM UserProfile up WHERE up.id = :profileId")
    <T> Optional<T> getUserProfileById(@Param("profileId") Long profileId, Class<T> clazz);

    @Query("SELECT up FROM UserProfile up WHERE up.user.id = :userId")
    List<ProfilePicUrlProjection> getUserProfileByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE UserProfile up SET up.isChosen = :isChosen WHERE up.user.id = :userId")
    void updateChosenProfilePic(@Param("isChosen") boolean isChosen, @Param("userId") UUID userId);
}
