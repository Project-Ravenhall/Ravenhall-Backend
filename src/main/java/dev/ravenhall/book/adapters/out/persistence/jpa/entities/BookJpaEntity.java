package dev.ravenhall.book.adapters.out.persistence.jpa.entities;

import dev.ravenhall.book.domain.enums.MetadataStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookJpaEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    // Nullable relationship: Standalone books have no series.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private BookSeriesJpaEntity series;

    @Column(name = "volume_number", precision = 6, scale = 2)
    private BigDecimal volumeNumber;

    @Column(name = "local_title", nullable = false, length = 500)
    private String localTitle;

    @Column(name = "local_authors")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> localAuthors;

    @Column(name = "api_title", length = 500)
    private String apiTitle;

    @Column(name = "api_authors")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> apiAuthors;

    @Column(name = "api_release_year")
    private Integer apiReleaseYear;

    @Column(name = "api_genres")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> apiGenres;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "cover_path", length = 500)
    private String coverPath;

    @Column(name = "metadata_status")
    @Enumerated(EnumType.STRING)
    private MetadataStatus metadataStatus;

    @Column(name = "added_at", updatable = false)
    private OffsetDateTime addedAt;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookAssetJpaEntity> assets = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookJpaEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Version
    private Long version;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void addAsset(BookAssetJpaEntity asset) {
        assets.add(asset);
        asset.setBook(this); // This forces the child to point back to this exact parent
    }

    // we implemented Persistable because we set the id in the domain
    // meaning it saves with a new version which means it doesn't do merge
    @Override
    public boolean isNew() {
        return this.version == null;
    }
}