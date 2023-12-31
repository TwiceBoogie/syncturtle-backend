// package dev.twiceb.passwordservice.controller.api;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import dev.twiceb.common.dto.response.HeaderResponse;
// import dev.twiceb.passwordservice.dto.response.AllPasswordsResponse;
// import dev.twiceb.passwordservice.service.PasswordService;
// import lombok.RequiredArgsConstructor;

// import static dev.twiceb.common.constants.PathConstants.*;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping(API_V1_DASHBOARD)
// public class PasswordApiController {

// private final PasswordService passwordService;

// @GetMapping(MAIN_EXPIRING_PASSWORDS)
// public HeaderResponse<AllPasswordsResponse>
// getExpiringPassword(@RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue =
// "0") Long userId) {
// return
// }
// }
