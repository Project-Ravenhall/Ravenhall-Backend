package dev.ravenhall.book.adapters.in.web.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RetrieveBookResponseDTO(
        UUID id,
        String localTitle,
        List<String> localAuthors,
        String apiTitle,
        List<String> apiAuthors,
        String synopsis,
        String coverPath,
        OffsetDateTime addedAt,
        Integer apiReleaseYear,
        List<String> apiGenres,
        List<AssetDTO> assets
) {
    public record AssetDTO(
            UUID id,
            String fileFormat,
            String filepath,
            int pageCount,
            String checksum,
            long fileSize,
            int syncedPage
    ) {}
}
