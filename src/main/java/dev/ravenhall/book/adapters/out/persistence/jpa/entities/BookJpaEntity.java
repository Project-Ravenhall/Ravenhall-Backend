package dev.ravenhall.book.adapters.out.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.*;

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
public class BookJpaEntity {

    @Id
    private UUID id;

    // Nullable relationship: Standalone books have no series.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private BookSeriesJpaEntity series;

    @Column(name = "volume_number", precision = 6, scale = 2)
    private BigDecimal volumeNumber;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 500)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "cover_path", length = 500)
    private String coverPath;

    @Column(name = "added_at", updatable = false)
    private OffsetDateTime addedAt;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAssetJpaEntity> assets = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookJpaEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    public void addAsset(BookAssetJpaEntity asset) {
        assets.add(asset);
        asset.setBook(this); // This forces the child to point back to this exact parent
    }
}