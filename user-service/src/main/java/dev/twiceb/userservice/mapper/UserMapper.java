package dev.twiceb.userservice.mapper;

import org.springframework.stereotype.Component;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.dto.response.UserMeResponse;
import dev.twiceb.userservice.dto.response.UserProfileResponse;
import dev.twiceb.userservice.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserService userService;
    private final BasicMapper mapper;

    public UserProfileResponse getProfile() {
        return mapper.convertToResponse(userService.getProfile(), UserProfileResponse.class);
    }

    public UserMeResponse getCurrentUser() {
        return mapper.convertToResponse(userService.getUser(), UserMeResponse.class);
    }

}
