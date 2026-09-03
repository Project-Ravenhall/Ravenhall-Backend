package dev.ravenhall.book.adapters.out.metadata.hardcover.mapper;

import dev.ravenhall.book.adapters.out.metadata.hardcover.dto.HardcoverDTO;
import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HardcoverMetadataMapper {

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "releaseYear", source = "releaseYear")
    @Mapping(target = "authors", source = "authorNames")
    @Mapping(target = "coverPath", source = "image.url")
    @Mapping(target = "genres", source = "genres")
    ApiMetadataResponse toDomain(HardcoverDTO.Document dto);
}


// MapStruct automatically uses this method to convert the JsonNode to a List<String>
//default List<String> mapContributors(JsonNode cachedContributors) {
//    if (cachedContributors == null || !cachedContributors.isArray()) {
//        return Collections.emptyList();
//    }
//
//    List<String> authors = new ArrayList<>();
//    for (JsonNode node : cachedContributors) {
//        JsonNode authorNode = node.get("authors");
//        if (authorNode != null && authorNode.hasNonNull("name")) {
//            authors.add(authorNode.get("name").asText());
//        }
//    }
//    return authors;
//}