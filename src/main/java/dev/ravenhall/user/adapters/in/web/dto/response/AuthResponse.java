package dev.ravenhall.user.adapters.in.web.dto.response;

// adapter/in/rest/dto/AuthResponse.java
public record AuthResponse(
        String token,
        UserResponse user
) {}
