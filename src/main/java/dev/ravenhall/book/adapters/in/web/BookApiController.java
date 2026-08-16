package dev.ravenhall.book.adapters.in.web;

import dev.ravenhall.book.adapters.in.web.dto.RetrieveBookResponseDTO;
import dev.ravenhall.book.adapters.in.web.mapper.BookRetrievalMapper;
import dev.ravenhall.book.application.ports.in.ManageBookPort;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import dev.ravenhall.book.domain.enums.FileFormat;
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


    @GetMapping("/{bookId}")
    public RetrieveBookResponseDTO retrieveBook(@PathVariable("bookId") UUID id) {
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


    @GetMapping("/{bookId}/cover")
    public ResponseEntity<Resource> retrieveBookCover(@PathVariable("bookId") UUID id) {
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

    @GetMapping("/assets/{assetId}")
    public ResponseEntity<Resource> retrieveBookAsset(@PathVariable("assetId") UUID id) {
        try {
            BookAsset asset = manageBookPort.retrieveAsset(id);
            if(asset == null || asset.getFilepath() == null) {
                return ResponseEntity.notFound().build();
            }

            Path assetPath = Paths.get(asset.getFilepath());

            // spring does resource.getInputStream() behind the scenes
            // which is how it is able to get the actual bytes
            Resource resource = new UrlResource(assetPath.toUri());

            // there is no MediaType.APPLICATION_EPUB we have to set it the manual way
            // which is also why its APPLICATION_PDF_VALUE and not APPLICATION_PDF
            String mimeType = asset.getFileFormat() == FileFormat.EPUB
                    ? "application/epub+zip"
                    : MediaType.APPLICATION_PDF_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
