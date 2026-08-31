package dev.ravenhall.book.adapters.out.persistence.jpa.entities.progress;

import dev.ravenhall.book.adapters.out.persistence.jpa.entities.BookAssetJpaEntity;
import dev.ravenhall.user.adapters.out.persistence.jpa.entites.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(BookProgressId.class)
public class BookProgressEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Id
    @ManyToOne
    @JoinColumn(name = "asset_id")
    private BookAssetJpaEntity asset;

    private int syncedPage;

    @NotNull
    @Column(nullable = false)
    private int version;
}
