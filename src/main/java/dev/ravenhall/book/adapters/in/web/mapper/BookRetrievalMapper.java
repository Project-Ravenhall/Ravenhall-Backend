package dev.ravenhall.book.adapters.in.web.mapper;

import dev.ravenhall.book.adapters.in.web.dto.RetrieveBookResponseDTO;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookRetrievalMapper {

    RetrieveBookResponseDTO domainToDto(Book book);

    @Mapping(source = "page", target = "syncedPage")
    RetrieveBookResponseDTO.AssetDTO assetToDto(BookAsset asset);

    // Add this: MapStruct will automatically generate the loop for you
    List<RetrieveBookResponseDTO> domainListToDtoList(List<Book> books);
}