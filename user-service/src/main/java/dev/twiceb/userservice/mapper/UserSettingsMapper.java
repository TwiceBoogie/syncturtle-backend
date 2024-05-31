package dev.twiceb.userservice.mapper;

import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.dto.request.SettingsRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.ProfilePicResponse;
import dev.twiceb.userservice.dto.response.UserPhoneResponse;
import dev.twiceb.userservice.repository.projection.ProfilePicUrlProjection;
import dev.twiceb.userservice.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserSettingsMapper {

    private final AuthenticationMapper authenticationMapper;
    private final UserSettingsService userSettingsService;
    private final BasicMapper mapper;

    public String updateUsername(SettingsRequest request, BindingResult bindingResult) {
        return userSettingsService.updateUsername(request.getUsername(), bindingResult);
    }

    public AuthenticationResponse updateEmail(SettingsRequest request, BindingResult bindingResult) {
        Map<String, Object> map = userSettingsService.updateEmail(request.getEmail(), bindingResult);
        AuthenticationResponse authenticationResponse = authenticationMapper.getAuthenticationResponse(map);
        authenticationResponse.getUser().setEmail(request.getEmail());
        return authenticationResponse;
    }

    public UserPhoneResponse updatePhone(SettingsRequest request, BindingResult bindingResult) {
        Map<String, Object> phoneParams = userSettingsService.updatePhone(request.getCountryCode(),
                request.getPhone(),
                bindingResult
        );
        return new UserPhoneResponse((String) phoneParams.get("countryCode"), (Long) phoneParams.get("phone"));
    }

    public String updateGender(SettingsRequest request, BindingResult bindingResult) {
        return userSettingsService.updateGender(request.getGender(), bindingResult);
    }

    public ProfilePicResponse updateProfilePic(Long newUserProfileId) {
        return mapper.convertToResponse(userSettingsService.updateProfilePic(newUserProfileId), ProfilePicResponse.class);
    }

    public HeaderResponse<ProfilePicResponse> uploadProfilePics(MultipartFile[] files) {
        List<ProfilePicUrlProjection> profilePics = userSettingsService.uploadProfilePic(files);
        return mapper.getHeaderResponse(profilePics, profilePics.size(), ProfilePicResponse.class);
    }

    public HeaderResponse<ProfilePicResponse> deleteProfilePic(Long userProfileId) {
        List<ProfilePicUrlProjection> profilePics = userSettingsService.deleteProfilePic(userProfileId);
        return mapper.getHeaderResponse(profilePics, profilePics.size(), ProfilePicResponse.class);
    }
}
