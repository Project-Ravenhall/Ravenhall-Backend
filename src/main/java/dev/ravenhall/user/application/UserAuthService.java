package dev.ravenhall.user.application;

import dev.ravenhall.security.TokenService;
import dev.ravenhall.user.application.ports.in.UserAuthUseCase;
import dev.ravenhall.user.application.ports.in.commands.LoginCommand;
import dev.ravenhall.user.application.ports.in.commands.SignupCommand;
import dev.ravenhall.user.application.ports.out.AuthenticationPort;
import dev.ravenhall.user.application.ports.out.UserPersistencePort;
import dev.ravenhall.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthService implements UserAuthUseCase {

    private final UserPersistencePort userPersistencePort;
    private final AuthenticationPort authenticationPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final Clock clock;

    @Override
    public AuthResult signup(SignupCommand command) {
        if (userPersistencePort.existsByUsername(command.username())) {
            throw new IllegalArgumentException("Username already taken: " + command.username());
        }

        log.info("Creating new user: {}", command.username());

        String hashedPassword = passwordEncoder.encode(command.password());
        User newUser = new User(command.username(), hashedPassword, command.displayName(), clock);

        userPersistencePort.saveUser(newUser);

        String token = tokenService.generateToken(newUser);
        return new AuthResult(token, newUser);
    }

    @Override
    public AuthResult login(LoginCommand login) {

            User user = authenticationPort.authenticate(login.username(), login.password());
            String token = tokenService.generateToken(user);
            return new AuthResult(token, user);
    }
}
