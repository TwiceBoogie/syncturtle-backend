package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.userservice.client.FileClient;
import dev.twiceb.userservice.domain.projection.ProfilePicUrlProjection;
import dev.twiceb.userservice.domain.repository.UserProfileRepository;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.service.AuthenticationService;
import dev.twiceb.userservice.service.UserSettingsService;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserSettingsServiceImpl implements UserSettingsService {

    private static final String USER_PROFILE_BUCKET = "USER";
    private static final int MAX_PROFILE_PIC_COUNT = 10;

    private final AuthenticationService authenticationService;
    private final UserServiceHelper userServiceHelper;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FileClient fileClient;
    private final JwtProvider jwtProvider;

    @Override
    public String updateUsername(String username, BindingResult bindingResult) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUsername'");
    }

    @Override
    public Map<String, Object> updateEmail(String email, BindingResult bindingResult) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateEmail'");
    }

    @Override
    public Map<String, Object> updatePhone(String countryCode, Long phone,
            BindingResult bindingResult) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePhone'");
    }

    @Override
    public String updateCountry(String country) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCountry'");
    }

    @Override
    public String updateGender(String gender, BindingResult bindingResult) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGender'");
    }

    @Override
    public String updateLanguage(String language) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateLanguage'");
    }

    @Override
    public ProfilePicUrlProjection updateProfilePic(Long newUserProfileId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateProfilePic'");
    }

    @Override
    public List<ProfilePicUrlProjection> uploadProfilePic(MultipartFile[] files) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'uploadProfilePic'");
    }

    @Override
    public List<ProfilePicUrlProjection> deleteProfilePic(Long userProfileId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteProfilePic'");
    }

}
