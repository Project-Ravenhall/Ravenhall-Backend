package dev.ravenhall.book.adapters.in.web.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RetrieveBookResponseDTO(
        UUID id,
        String title,
        String author,
        OffsetDateTime addedAt
) {

}
