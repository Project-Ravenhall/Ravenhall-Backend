package dev.ravenhall.book.adapters.out.metadata.google.dto;

import java.util.List;

public record GoogleBooksResponseDTO(
        List<Item> items
) {
    public record Item(
            VolumeInfo volumeInfo
    ) {}

    public record VolumeInfo(
            String title,
            List<String> authors,
            String description,
            ImageLinks imageLinks,
            String publishedDate
    ) {}

    public record ImageLinks(
            String thumbnail
    ) {}
}