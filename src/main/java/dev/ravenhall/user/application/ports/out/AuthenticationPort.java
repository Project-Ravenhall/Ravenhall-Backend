package dev.ravenhall.user.application.ports.out;

import dev.ravenhall.user.domain.User;

public interface AuthenticationPort {

    User authenticate(String username, String password);
}
