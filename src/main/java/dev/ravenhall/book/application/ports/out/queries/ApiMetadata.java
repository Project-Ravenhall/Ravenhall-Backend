package dev.ravenhall.book.application.ports.out.queries;

import java.util.List;

public record ApiMetadata(
        String title,
        List<String> authors,
        String description,
        String publishedDate,
        String coverPath
) {}
