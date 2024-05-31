package dev.twiceb.userservice.controller.rest;

import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.userservice.controller.RegistrationControllerSwagger;
import dev.twiceb.userservice.dto.request.ProcessEmailRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.dto.response.RegistrationEndResponse;
import dev.twiceb.userservice.mapper.RegistrationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_AUTH)
public class RegistrationController implements RegistrationControllerSwagger {

    private final RegistrationMapper registrationMapper;

    @Override
    @PostMapping(REGISTRATION_CHECK)
    public ResponseEntity<GenericResponse> registration(RegistrationRequest request, BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationMapper.registration(request, bindingResult));
    }

    @Override
    @PostMapping(REGISTRATION_CODE)
    public ResponseEntity<GenericResponse> sendRegistrationCode(ProcessEmailRequest request, BindingResult bindingResult) {
        return ResponseEntity.ok(registrationMapper.sendRegistrationCode(request.getEmail(), bindingResult));
    }

    @Override
    @GetMapping(REGISTRATION_ACTIVATE_CODE)
    public ResponseEntity<RegistrationEndResponse> checkRegistrationCode(@PathVariable String code) {
        return ResponseEntity.ok(registrationMapper.checkRegistrationCode(code));
    }
}
