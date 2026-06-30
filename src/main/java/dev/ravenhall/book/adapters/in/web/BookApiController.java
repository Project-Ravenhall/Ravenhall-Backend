package dev.ravenhall.book.adapters.in.web;

import dev.ravenhall.book.adapters.in.web.dto.RetrieveBookResponseDTO;
import dev.ravenhall.book.adapters.in.web.mapper.BookRetrievalMapper;
import dev.ravenhall.book.application.ports.in.ManageBookUseCase;
import dev.ravenhall.book.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BookApiController {

    private final ManageBookUseCase manageBookUseCase;
    private final BookRetrievalMapper bookRetrievalMapper;


    @GetMapping("/{id}")
    public RetrieveBookResponseDTO retrieveBook(@PathVariable("id") UUID id) {
        Book bookDomain = manageBookUseCase.retrieveBook(id);
        return bookRetrievalMapper.domainToDto(bookDomain);
    }


}