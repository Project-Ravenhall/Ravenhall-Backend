package dev.ravenhall.book.application.ports.out.queries;

import java.util.List;

public record ApiMetadata(
        String title,
        List<String> authors,
        String description,
        Integer releaseYear,
        String coverPath,
        List<String> genres
) {}
