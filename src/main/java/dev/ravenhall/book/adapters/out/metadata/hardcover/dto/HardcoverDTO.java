package dev.ravenhall.book.adapters.out.metadata.hardcover.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record HardcoverDTO(
        List<Hit> hits
) {
    public record Hit(Document document) {}

    public record Document(
            Integer id,
            String title,
            @JsonProperty("author_names") List<String> authorNames,
            String description,
            @JsonProperty("release_year") Integer releaseYear,
            Image image,
            List<String> genres
    ) {}

    public record Image(String url) {}
}

