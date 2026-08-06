package dev.ravenhall.user.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class User {

    private final UUID id;
    private final String username;
    private String passwordHash;
    private String displayName;
    private final Long version;
    private final OffsetDateTime createdAt;

    // CREATION: this constructor is used for creation only, therefore the id and time is generated
    public User(String username, String passwordHash, String displayName, Clock clock) {
        this.id = UUID.randomUUID();
        this.username = username.trim();
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.version = null;
        this.createdAt = OffsetDateTime.now(clock);
    }

    // RECONSTITUTION: this is for reconstitution only, like loading from DB, and also why we don't need `Clock` here
    public User(UUID id, String username, String passwordHash, String displayName, Long version, OffsetDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.version = version;
        this.createdAt = createdAt;
    }
}
