package dev.ravenhall.user.application.ports.in.commands;

// application/ports/in/commands/LoginCommand.java
public record LoginCommand(
        String username,
        String password
) {}
