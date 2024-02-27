package dev.twiceb.userservice.service;

import dev.twiceb.userservice.repository.projection.ProfilePicUrlProjection;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserSettingsService {
    String updateUsername(String username);
    Map<String, Object> updateEmail(String email);
    Map<String, Object> updatePhone(String countryCode, Long phone);
    String updateCountry(String country);
    String updateGender(String gender);
    String updateLanguage(String language);
    ProfilePicUrlProjection updateProfilePic(Long newUserProfileId);
    List<ProfilePicUrlProjection> uploadProfilePic(MultipartFile[] files);
    List<ProfilePicUrlProjection> deleteProfilePic(Long userProfileId);
}
