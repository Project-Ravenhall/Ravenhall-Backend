package dev.ravenhall.user.adapters.out.persistence.jpa.entites;

import dev.ravenhall.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements Persistable<UUID> {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "display_name")
    private String displayName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    // we (or I really there's only me) used Long or specifically Object so we can check them against null in isNew()
    @Version
    private Long version;

    public User toDomain() {
        return new User(id, username, passwordHash, displayName, version, createdAt);
    }

    // we implemented Persistable because we set the id in the domain
    // meaning it saves with a new version which means it doesn't do merge (SELECT then-> INSERT)
    @Override
    @Transient
    public boolean isNew() {
        return this.version == null;
    }
}
