package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.request.FileImageRequest;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.userservice.feign.FileClient;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.model.UserProfile;
import dev.twiceb.userservice.repository.UserProfileRepository;
import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.repository.projection.AuthUserProjection;
import dev.twiceb.userservice.repository.projection.ProfilePicUrlProjection;
import dev.twiceb.userservice.service.AuthenticationService;
import dev.twiceb.userservice.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class UserSettingsServiceImpl implements UserSettingsService {

    private static final String USER_PROFILE_BUCKET = "USER";
    private static final int MAX_PROFILE_PIC_COUNT = 10;

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FileClient fileClient;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public String updateUsername(String username) {
        if (username.isEmpty() || username.length() > 50) {
            throw new ApiRequestException(INCORRECT_USERNAME_LENGTH, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsUserByUsername(username)) {
            throw new ApiRequestException(USERNAME_ALREADY_TAKEN, HttpStatus.BAD_REQUEST);
        }

        User user = authenticationService.getAuthenticatedUser();
        user.setUsername(username);
        userRepository.save(user);

        return username;
    }

    @Override
    @Transactional
    public Map<String, Object> updateEmail(String email) {
        if (userRepository.isUserExistByEmail(email)) {
            throw new ApiRequestException(EMAIL_ALREADY_TAKEN, HttpStatus.BAD_REQUEST);
        }
        Long authUserId = authenticationService.getAuthenticatedUserId();
        userRepository.updateEmail(email, authUserId);
        String token = jwtProvider.createToken(email, "USER");
        AuthUserProjection user = userRepository.getAuthUserProjection(authUserId);

        return Map.of("user", user, "token", token);
    }

    @Override
    @Transactional
    public Map<String, Object> updatePhone(String countryCode, Long phone) {
        int phoneLength = String.valueOf(phone).length();

        if (phoneLength < 6 || phoneLength > 10) {
            throw new ApiRequestException(INVALID_PHONE_NUMBER, HttpStatus.BAD_REQUEST);
        }
        Long authUserId = authenticationService.getAuthenticatedUserId();
        userRepository.updatePhone(countryCode, phone, authUserId);

        return Map.of("countryCode", countryCode, "phone", phone);
    }

    @Override
    public String updateCountry(String country) {
        return null;
    }

    @Override
    @Transactional
    public String updateGender(String gender) {
        if (gender.isEmpty() || gender.length() > 30) {
            throw new ApiRequestException(INVALID_GENDER_LENGTH, HttpStatus.BAD_REQUEST);
        }
        Long authUserId = authenticationService.getAuthenticatedUserId();
        userRepository.updateGender(gender, authUserId);
        return gender;
    }

    @Override
    public String updateLanguage(String language) {
        return null;
    }

    @Override
    @Transactional
    public ProfilePicUrlProjection updateProfilePic(Long newUserProfileId) {
        User user = authenticationService.getAuthenticatedUser();
        List<UserProfile> profilePics = user.getUserProfiles();

        profilePics.forEach(profilePic -> {
            profilePic.setChosen(profilePic.getId().equals(newUserProfileId));
        });
        userRepository.save(user);

        return userProfileRepository.getUserProfileById(newUserProfileId, ProfilePicUrlProjection.class)
                .orElseThrow(() -> new ApiRequestException(NO_RESOURCE_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public List<ProfilePicUrlProjection> uploadProfilePic(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new ApiRequestException("No files provided for upload.", HttpStatus.BAD_REQUEST);
        }
        User authUser = authenticationService.getAuthenticatedUser();
        int profileCount = authUser.getUserProfileLimt().getProfileCount();
        if (profileCount > MAX_PROFILE_PIC_COUNT || (profileCount + files.length) > MAX_PROFILE_PIC_COUNT) {
            throw new ApiRequestException("You can only have 10 profile pics at a time", HttpStatus.BAD_REQUEST);
        }
        List<String> imageUrls = fileClient.uploadImages(files, USER_PROFILE_BUCKET);
        for (int i = 0; i < imageUrls.size(); i++) {
            MultipartFile file = files[i];
            UserProfile userProfile = new UserProfile(
                    authUser,
                    file.getOriginalFilename(),
                    imageUrls.get(i),
                    file.getContentType()
            );
            authUser.getUserProfiles().add(userProfile);
        }
        authUser.getUserProfileLimt().setProfileCount(profileCount + imageUrls.size());
        userRepository.save(authUser);

        return userProfileRepository.getUserProfileByUserId(authUser.getId());
    }

    @Override
    @Transactional
    public List<ProfilePicUrlProjection> deleteProfilePic(Long userProfileId) {
        User authUser = authenticationService.getAuthenticatedUser();
        List<UserProfile> profilePics = authUser.getUserProfiles();
        UserProfile profilePicToDelete = null;
        for (UserProfile profilePic : profilePics) {
            if (profilePic.getId().equals(userProfileId)) {
                profilePicToDelete = profilePic;
            }
        }
        if (profilePicToDelete == null) {
            throw new ApiRequestException(NO_RESOURCE_FOUND, HttpStatus.NOT_FOUND);
        }
        FileImageRequest request = new FileImageRequest();
        request.setImageUrl(profilePicToDelete.getFilePath());
        fileClient.deleteFileImage(request, USER_PROFILE_BUCKET);

        profilePics.remove(profilePicToDelete);
        authUser.getUserProfileLimt().setProfileCount(profilePics.size());
        userRepository.save(authUser);

        return userProfileRepository.getUserProfileByUserId(authUser.getId());
    }
}
