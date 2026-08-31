package dev.ravenhall.security.model;

import java.util.UUID;

public record JwtPrincipal(
        String username,
        UUID userId
) {
}
