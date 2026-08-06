package dev.ravenhall.book.adapters.in.web;

import dev.ravenhall.book.adapters.in.web.dto.RetrieveBookResponseDTO;
import dev.ravenhall.book.adapters.in.web.mapper.BookRetrievalMapper;
import dev.ravenhall.book.application.ports.in.ManageBookPort;
import dev.ravenhall.book.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BookApiController {

    private final ManageBookPort manageBookPort;
    private final BookRetrievalMapper bookRetrievalMapper;


    @GetMapping("/{id}")
    public RetrieveBookResponseDTO retrieveBook(@PathVariable("id") UUID id) {
        Book bookDomain = manageBookPort.retrieveBook(id);
        return bookRetrievalMapper.domainToDto(bookDomain);
    }

    @GetMapping("/allBooks")
    public List<RetrieveBookResponseDTO> retrieveAllBooks() {
        List<Book> books = manageBookPort.retrieveAllBooks();
        List<RetrieveBookResponseDTO> dtos = new ArrayList<>();
        for(Book book : books) {
            dtos.add(bookRetrievalMapper.domainToDto(book));
        }
        return dtos;
    }


    @GetMapping("/{id}/cover")
    public ResponseEntity<Resource> retrieveBookCover(@PathVariable("id") UUID id) {
        try {
            Book book = manageBookPort.retrieveBook(id);
            if (book == null || book.getCoverPath() == null) {
                return ResponseEntity.notFound().build();
            }

            Path coverPath = Paths.get(book.getCoverPath());
            Resource resource = new UrlResource(coverPath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }
}