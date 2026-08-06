package dev.ravenhall.user.adapters.out.persistence.jpa;

import dev.ravenhall.user.adapters.out.persistence.jpa.entites.UserEntity;
import dev.ravenhall.user.adapters.out.persistence.jpa.repository.UserRepository;
import dev.ravenhall.user.application.ports.out.UserPersistencePort;
import dev.ravenhall.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        UserEntity entity = new UserEntity(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getDisplayName(),
                user.getCreatedAt(),
                user.getVersion()
        );
        userRepository.save(entity);
        log.debug("Saved user with id: {}", user.getId());
    }

    @Override
    public User getByUsername(String username) {
        Optional<UserEntity> entity = userRepository.findByUsername(username);

        return entity.map(e -> new User(
                e.getId(),
                e.getUsername(),
                e.getPasswordHash(),
                e.getDisplayName(),
                e.getVersion(),
                e.getCreatedAt()
        )).orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
