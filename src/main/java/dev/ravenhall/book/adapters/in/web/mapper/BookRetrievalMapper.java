package dev.ravenhall.book.adapters.in.web.mapper;

import dev.ravenhall.book.adapters.in.web.dto.RetrieveBookResponseDTO;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookRetrievalMapper {

    @Mapping(target = "coverUrl", expression = "java(book.getCoverPath() != null ? \"/api/v1/books/\" + book.getId() + \"/cover\" : null)")
    RetrieveBookResponseDTO domainToDto(Book book);

    @Mapping(source = "page", target = "syncedPage")
    @Mapping(target = "fileUrl", expression = "java(\"/api/v1/books/assets/\" + asset.getId())")
    RetrieveBookResponseDTO.AssetDTO assetToDto(BookAsset asset);

    List<RetrieveBookResponseDTO> domainListToDtoList(List<Book> books);
}