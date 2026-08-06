package dev.ravenhall.security;

import dev.ravenhall.user.adapters.out.persistence.jpa.entites.UserEntity;
import dev.ravenhall.user.adapters.out.persistence.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RavenUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @NullMarked // spring boot 3 introduced @NullMark, so when we override we have to promise non nullability too
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserEntity::toDomain)    // this converts JPA Entity to pure Domain User
                .map(SecurityUser::new)               // wraps User in SecurityUser
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
