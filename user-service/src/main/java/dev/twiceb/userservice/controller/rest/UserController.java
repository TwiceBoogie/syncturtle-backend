package dev.twiceb.userservice.controller.rest;

import dev.twiceb.userservice.controller.UserControllerSwagger;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.twiceb.common.constants.PathConstants.UI_V1_USER;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_USER)
public class UserController implements UserControllerSwagger {

    private final AuthenticationMapper authenticationMapper;

    @Override
    public ResponseEntity<AuthenticationResponse> getUserByToken() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserByToken'");
    }

}
