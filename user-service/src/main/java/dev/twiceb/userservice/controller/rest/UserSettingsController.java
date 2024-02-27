package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.userservice.dto.request.SettingsRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.ProfilePicResponse;
import dev.twiceb.userservice.dto.response.UserPhoneResponse;
import dev.twiceb.userservice.mapper.UserSettingsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_USER_SETTINGS_UPDATE)
public class UserSettingsController {

    private final UserSettingsMapper userSettingsMapper;

    @PutMapping(USERNAME)
    public ResponseEntity<String> updateUsername(@RequestBody SettingsRequest request) {
        return ResponseEntity.ok(userSettingsMapper.updateUsername(request));
    }

    @PutMapping(EMAIL)
    public ResponseEntity<AuthenticationResponse> updateEmail(@RequestBody SettingsRequest request) {
        return ResponseEntity.ok(userSettingsMapper.updateEmail(request));
    }

    @PutMapping(PHONE)
    public ResponseEntity<UserPhoneResponse> updatePhone(@RequestBody SettingsRequest request) {
        return ResponseEntity.ok(userSettingsMapper.updatePhone(request));
    }

    @PutMapping(GENDER)
    public ResponseEntity<String> updateGender(@RequestBody SettingsRequest request) {
        return ResponseEntity.ok(userSettingsMapper.updateGender(request));
    }

    @PutMapping(SET_AVATAR)
    public ResponseEntity<ProfilePicResponse> updateProfilePic(@PathVariable("userProfileId") Long userProfileId) {
        return ResponseEntity.ok(userSettingsMapper.updateProfilePic(userProfileId));
    }

    @PostMapping(AVATAR)
    public ResponseEntity<List<ProfilePicResponse>> uploadProfilePics(@RequestPart("files") MultipartFile[] files) {
        HeaderResponse<ProfilePicResponse> res = userSettingsMapper.uploadProfilePics(files);
        return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
    }

    @DeleteMapping(SET_AVATAR)
    public ResponseEntity<List<ProfilePicResponse>> deleteProfilePic(@PathVariable("userProfileId") Long userProfileId) {
        HeaderResponse<ProfilePicResponse> res = userSettingsMapper.deleteProfilePic(userProfileId);
        return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
    }
}
