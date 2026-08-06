package dev.ravenhall.user.application.ports.out;

import dev.ravenhall.user.domain.User;

public interface UserPersistencePort {

    void saveUser(User user);
    User getByUsername(String username);
    boolean existsByUsername(String username);
}
