package dev.ravenhall.book.domain;

import lombok.Getter;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Book {

    private final UUID id;
    private final String title;
    private final OffsetDateTime addedAt;
    private final String author;
    private final UUID seriesId;
    private final List<BookAsset> assets = new ArrayList<>();

    // 1. THE CREATION CONSTRUCTOR (Used by the Scanner/Service)
    public Book(UUID id, String title, Clock clock, String author, UUID seriesId) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.title = title.trim();
        this.addedAt = OffsetDateTime.now(clock);
        this.author = author.trim();
        this.seriesId = seriesId;
    }

    public Book(UUID id, String title, OffsetDateTime addedAt, String author, UUID seriesId) {
        this.id = id;
        this.title = title;
        this.addedAt = addedAt;
        this.author = author;
        this.seriesId = seriesId;
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