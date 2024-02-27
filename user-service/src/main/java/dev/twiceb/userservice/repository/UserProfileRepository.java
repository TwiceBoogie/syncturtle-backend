package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.UserProfile;
import dev.twiceb.userservice.repository.projection.ProfilePicUrlProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query("SELECT up FROM UserProfile up WHERE up.id = :profileId")
    <T> Optional<T> getUserProfileById(@Param("profileId") Long profileId, Class<T> clazz);

    @Query("SELECT up FROM UserProfile up WHERE up.user.id = :userId")
    List<ProfilePicUrlProjection> getUserProfileByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserProfile up SET up.isChosen = :isChosen WHERE up.user.id = :userId")
    void updateChosenProfilePic(@Param("isChosen") boolean isChosen, @Param("userId") Long userId);
}
