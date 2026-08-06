package dev.ravenhall.user.application.ports.in.commands;

public record SignupCommand(
        String username,
        String password,
        String displayName
) {}
