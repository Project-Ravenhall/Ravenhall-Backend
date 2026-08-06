package dev.ravenhall.user.application;

import dev.ravenhall.user.domain.User;
public record AuthResult(String token, User user) {}
