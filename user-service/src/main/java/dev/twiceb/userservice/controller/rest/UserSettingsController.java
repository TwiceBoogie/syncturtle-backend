package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.userservice.controller.UserSettingsControllerSwagger;
import dev.twiceb.userservice.dto.request.SettingsRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.ProfilePicResponse;
import dev.twiceb.userservice.dto.response.UserPhoneResponse;
import dev.twiceb.userservice.mapper.UserSettingsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_USER_SETTINGS_UPDATE)
public class UserSettingsController implements UserSettingsControllerSwagger {

    private final UserSettingsMapper userSettingsMapper;

    @Override
    public ResponseEntity<String> updateUsername(SettingsRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(userSettingsMapper.updateUsername(request, bindingResult));
    }

    @Override
    public ResponseEntity<AuthenticationResponse> updateEmail(SettingsRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(userSettingsMapper.updateEmail(request, bindingResult));
    }

    @Override
    public ResponseEntity<UserPhoneResponse> updatePhone(SettingsRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(userSettingsMapper.updatePhone(request, bindingResult));
    }

    @Override
    public ResponseEntity<String> updateGender(SettingsRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(userSettingsMapper.updateGender(request, bindingResult));
    }

    @Override
    public ResponseEntity<ProfilePicResponse> updateProfilePic(@PathVariable Long userProfileId) {
        return ResponseEntity.ok(userSettingsMapper.updateProfilePic(userProfileId));
    }

    @Override
    public ResponseEntity<List<ProfilePicResponse>> uploadProfilePics(@RequestPart("files") MultipartFile[] files) {
        HeaderResponse<ProfilePicResponse> res = userSettingsMapper.uploadProfilePics(files);
        return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
    }

    @Override
    public ResponseEntity<List<ProfilePicResponse>> deleteProfilePic(
            @PathVariable Long userProfileId) {
        HeaderResponse<ProfilePicResponse> res = userSettingsMapper.deleteProfilePic(userProfileId);
        return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
    }
}
