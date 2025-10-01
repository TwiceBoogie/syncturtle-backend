package dev.twiceb.userservice.controller.rest;

import dev.twiceb.userservice.controller.UserControllerSwagger;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.UserMeResponse;
import dev.twiceb.userservice.dto.response.UserProfileResponse;
import dev.twiceb.userservice.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.twiceb.common.constants.PathConstants.USERS;

@RestController
@RequiredArgsConstructor
@RequestMapping(USERS)
public class UserController implements UserControllerSwagger {

    private final UserMapper userMapper;

    @Override
    public ResponseEntity<AuthenticationResponse> getUserByToken() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserByToken'");
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getCurrentUser() {
        return ResponseEntity.ok().body(userMapper.getCurrentUser());
    }

    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok().body(userMapper.getProfile());
    }

}
