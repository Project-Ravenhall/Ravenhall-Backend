package dev.ravenhall.book.domain;

import dev.ravenhall.book.domain.enums.MetadataStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Book {

    private final UUID id;
    private final String localTitle;
    private String synopsis;
    private final OffsetDateTime addedAt;
    private List<String> localAuthors;
    private String coverPath;
    private final UUID seriesId;
    private final Long version;
    private MetadataStatus metadataStatus;
    private String apiTitle;
    private List<String> apiAuthors;
    private final List<BookAsset> assets = new ArrayList<>();

    // this constructor is used for creation only, therefore the id is randomly set
    public Book(String localTitle, Clock clock, List<String> localAuthors, UUID seriesId, MetadataStatus metadataStatus) {
        this.id = UUID.randomUUID();
        this.localTitle = localTitle.trim();
        this.addedAt = OffsetDateTime.now(clock);
        this.localAuthors = localAuthors;
        this.seriesId = seriesId;
        this.version = null;
        this.metadataStatus = metadataStatus;
    }

    // this is for reconstitution only, like loading from DB
    public Book(UUID id, String localTitle, Long version, List<String> localAuthors, String synopsis, String coverPath, UUID seriesId, OffsetDateTime addedAt, String apiTitle, List<String> apiAuthors, MetadataStatus metadataStatus) {
        this.id = id;
        this.localTitle = localTitle;
        this.version = version;
        this.addedAt = addedAt;
        this.localAuthors = localAuthors;
        this.synopsis = synopsis;
        this.coverPath = coverPath;
        this.seriesId = seriesId;
        this.apiTitle = apiTitle;
        this.apiAuthors = apiAuthors;
        this.metadataStatus = metadataStatus;
    }

    public void addAsset(BookAsset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Cannot add a null asset.");
        }

        boolean isDuplicate = assets.stream().anyMatch(exists -> exists.getChecksum().equals(asset.getChecksum()));
        if (isDuplicate) {
            throw new IllegalArgumentException("Asset is a duplicate: " + asset.getChecksum());
        }
        assets.add(asset);
    }

    public List<BookAsset> getAssets() {
        return List.copyOf(assets);
    }
}