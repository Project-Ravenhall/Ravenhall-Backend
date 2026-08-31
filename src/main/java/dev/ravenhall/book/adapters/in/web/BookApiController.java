package dev.ravenhall.book.adapters.in.web;

import dev.ravenhall.book.adapters.in.web.dto.ProgressDTO;
import dev.ravenhall.book.adapters.in.web.dto.RetrieveBookResponseDTO;
import dev.ravenhall.book.adapters.in.web.mapper.BookRetrievalMapper;
import dev.ravenhall.book.adapters.in.web.mapper.ProgressMapper;
import dev.ravenhall.book.application.ports.in.ManageBookUseCase;
import dev.ravenhall.book.application.ports.in.data.progress.ProgressUpdate;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import dev.ravenhall.book.domain.Progress;
import dev.ravenhall.book.domain.enums.FileFormat;
import dev.ravenhall.security.model.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BookApiController {

    private final ManageBookUseCase manageBookUseCase;
    private final BookRetrievalMapper bookRetrievalMapper;
    private final ProgressMapper progressMapper;


    @GetMapping("/{bookId}")
    public RetrieveBookResponseDTO retrieveBook(
            @AuthenticationPrincipal JwtPrincipal principal, 
            @PathVariable("bookId") UUID id) 
    {
        Book bookDomain = manageBookUseCase.retrieveBook(id, principal.userId());
        return bookRetrievalMapper.domainToDto(bookDomain);
    }

    @GetMapping("/allBooks")
    public List<RetrieveBookResponseDTO> retrieveAllBooks() {
        List<Book> books = manageBookUseCase.retrieveAllBooks();
        return bookRetrievalMapper.domainListToDtoList(books);
    }


    @GetMapping("/{bookId}/cover")
    public ResponseEntity<Resource> retrieveBookCover(@PathVariable("bookId") UUID id) {
        try {
            Book book = manageBookUseCase.retrieveBook(id, null);
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
            BookAsset asset = manageBookUseCase.retrieveAsset(id);
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

    @PostMapping("/assets/{assetId}/progress")
    public ResponseEntity<ProgressDTO> updateProgress(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable("assetId") UUID assetId,
            @RequestBody ProgressDTO request) {
        
        ProgressUpdate update = new ProgressUpdate(principal.userId(), assetId, request.page(), request.version());
        Progress progress = manageBookUseCase.updateProgress(update);
        return ResponseEntity.ok(progressMapper.domainToDto(progress));
    }

    @GetMapping("/assets/{assetId}/progress")
    @NullMarked
    public ResponseEntity<ProgressDTO> getProgress(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable("assetId") UUID assetId) {

        Progress progress = manageBookUseCase.getProgressForAsset(principal.userId(), assetId);
        return ResponseEntity.ok(progressMapper.domainToDto(progress));
    }
}
