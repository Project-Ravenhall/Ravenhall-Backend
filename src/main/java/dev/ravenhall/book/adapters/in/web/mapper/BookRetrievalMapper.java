package dev.ravenhall.book.adapters.in.web.mapper;

import dev.ravenhall.book.adapters.in.web.dto.RetrieveBookResponseDTO;
import dev.ravenhall.book.domain.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookRetrievalMapper {

    // MapStruct will automatically generate the implementation class
    // and map id -> id, title -> title, etc.
    RetrieveBookResponseDTO domainToDto(Book book);

}