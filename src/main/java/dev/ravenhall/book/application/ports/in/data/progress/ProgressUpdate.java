package dev.ravenhall.book.application.ports.in.data.progress;

import java.util.UUID;

public record ProgressUpdate(UUID userId, UUID assetId, int latestPage, int version) {
}
