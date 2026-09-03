package dev.ravenhall.security.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class JwtSecretProvider {

    @Value("${jwt.secret.storage.path:/data/secrets/jwt.secret}")
    private String secretPath;

    @Getter
    private String secret;

    @PostConstruct
    public void init() {
        secret = resolveSecret();
    }

    private String resolveSecret() {
        String envSecret = System.getenv("JWT_SECRET");
        if (envSecret != null && !envSecret.isBlank()) {
            log.info("Loaded JWT secret from JWT_SECRET environment variable");
            return envSecret;
        }

        try {
            Path path = Path.of(secretPath);
            if (Files.exists(path)) {
                log.info("Loaded existing JWT secret from: {}", path);
                return Files.readString(path).trim();
            }

            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            String generated = Base64.getEncoder().encodeToString(bytes);

            Files.createDirectories(path.getParent());
            Files.writeString(path, generated);
            log.info("Generated JWT Secret at: {}", path);
            return generated;
        } catch (IOException e) {
            log.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new IllegalStateException("Failed to load or generate JWT secret", e);
        }
    }
}