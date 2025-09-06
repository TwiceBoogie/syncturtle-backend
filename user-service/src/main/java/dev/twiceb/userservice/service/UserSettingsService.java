package dev.twiceb.userservice.service;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import dev.twiceb.userservice.domain.projection.ProfilePicUrlProjection;
import java.util.List;
import java.util.Map;

public interface UserSettingsService {
    String updateUsername(String username, BindingResult bindingResult);

    Map<String, Object> updateEmail(String email, BindingResult bindingResult);

    Map<String, Object> updatePhone(String countryCode, Long phone, BindingResult bindingResult);

    String updateCountry(String country);

    String updateGender(String gender, BindingResult bindingResult);

    String updateLanguage(String language);

    ProfilePicUrlProjection updateProfilePic(Long newUserProfileId);

    List<ProfilePicUrlProjection> uploadProfilePic(MultipartFile[] files);

    List<ProfilePicUrlProjection> deleteProfilePic(Long userProfileId);
}
