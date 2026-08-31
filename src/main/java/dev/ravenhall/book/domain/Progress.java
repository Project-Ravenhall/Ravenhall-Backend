package dev.ravenhall.book.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Progress {

    private final UUID userId;
    private final UUID assetId;
    private final int page;
    private final int version;

    public Progress(UUID userId, UUID assetId, int page, int version) {
        this.userId = userId;
        this.assetId = assetId;
        this.page = page;
        this.version = version;
    }
}
