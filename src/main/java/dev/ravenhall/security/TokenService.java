package dev.ravenhall.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.ravenhall.security.config.JwtSecretProvider;
import dev.ravenhall.security.exceptions.InvalidTokenException;
import dev.ravenhall.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtSecretProvider jwtSecretProvider;

    private static final String ISSUER = "ravenhall";

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecretProvider.getSecret());
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getUsername())
                    .withClaim("userId", user.getId().toString())
                    .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new InvalidTokenException("Failed to create exception", e);
        }
    }

    public DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecretProvider.getSecret());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("Token verification failed: " + e.getMessage(), e);
        }
    }
}
