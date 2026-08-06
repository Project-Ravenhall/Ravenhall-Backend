package dev.ravenhall.user.adapters.out.auth;

import dev.ravenhall.security.SecurityUser;
import dev.ravenhall.user.application.ports.out.AuthenticationPort;
import dev.ravenhall.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SpringAuthenticationAdapter implements AuthenticationPort {

    private final AuthenticationManager authenticationManager;

    @Override
    public User authenticate(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        if(auth.getPrincipal() instanceof SecurityUser(User user)) {
            return user;
        }

        throw new UsernameNotFoundException(username);

    }
}
