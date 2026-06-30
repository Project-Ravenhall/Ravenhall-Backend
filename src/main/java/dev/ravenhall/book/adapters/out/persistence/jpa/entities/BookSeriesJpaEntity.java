package dev.ravenhall.book.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "book_series")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookSeriesJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 500)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "cover_path", length = 500)
    private String coverPath;

    @Column(name = "added_at", insertable = false, updatable = false)
    private OffsetDateTime addedAt;

    // One Series has Many Books. Cascade applies if you delete a series.
    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookJpaEntity> books = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookSeriesJpaEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}