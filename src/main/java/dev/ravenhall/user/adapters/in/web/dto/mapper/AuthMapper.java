package dev.ravenhall.user.adapters.in.web.dto.mapper;

import dev.ravenhall.user.adapters.in.web.dto.response.AuthResponse;
import dev.ravenhall.user.adapters.in.web.dto.response.UserResponse;
import dev.ravenhall.user.application.AuthResult;
import dev.ravenhall.user.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    UserResponse toUserResponse(User user);

    AuthResponse toResponse(AuthResult result);
}
