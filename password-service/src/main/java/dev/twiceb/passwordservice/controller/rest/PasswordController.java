package dev.twiceb.passwordsservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequestMapping(UI_V1_DASHBOARD)
public class PasswordController {

    @PostMapping(CREATE_PASSWORD)
    public ResponseEntity<GenericResponse> createNewPassword(@RequestHeader(AUTH_USER_ID_HEADER) String userId) {
        GenericResponse res = new GenericResponse();
        res.setMessage(userId);
        return ResponseEntity.ok(res);
    }
}
