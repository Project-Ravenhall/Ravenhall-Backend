package dev.ravenhall.user.adapters.in.web.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String displayName,
        Long version
) {}