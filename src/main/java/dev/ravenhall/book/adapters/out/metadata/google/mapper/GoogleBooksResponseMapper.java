package dev.ravenhall.book.adapters.out.metadata.google.mapper;

import dev.ravenhall.book.adapters.out.metadata.google.dto.GoogleBooksResponseDTO;
import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoogleBooksResponseMapper {

    default ApiMetadataResponse DTOtoDomain(GoogleBooksResponseDTO responseDTO)
    {
        if (responseDTO.items() == null || responseDTO.items().isEmpty()) return null;

        GoogleBooksResponseDTO.Item firstItem = responseDTO.items().getFirst();
        GoogleBooksResponseDTO.VolumeInfo volumeInfo = firstItem.volumeInfo();
        GoogleBooksResponseDTO.ImageLinks imageLinks = volumeInfo.imageLinks();

        String title = volumeInfo.title();
        List<String> authors = volumeInfo.authors();
        String description = volumeInfo.description();
        String coverPath = imageLinks.thumbnail().replaceAll("zoom=\\d+", "zoom=3");

        return new ApiMetadataResponse(title, authors, description, null, coverPath, null);
    }
}
