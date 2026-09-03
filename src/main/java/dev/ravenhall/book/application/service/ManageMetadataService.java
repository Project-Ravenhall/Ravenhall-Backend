package dev.ravenhall.book.application.service;

import dev.ravenhall.book.application.ports.in.ManageMetadataUseCase;
import dev.ravenhall.book.application.ports.out.ManageBookPort;
import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataResponse;
import dev.ravenhall.book.application.ports.out.data.metadata.api.ApiMetadataSearchRequest;
import dev.ravenhall.book.application.ports.out.metadata.DownloadBookCoverPort;
import dev.ravenhall.book.application.ports.out.metadata.FetchBookMetadataPort;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.enums.MetadataStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManageMetadataService implements ManageMetadataUseCase {

    private final FetchBookMetadataPort metadataPort;
    private final DownloadBookCoverPort downloadCoverPort;
    private final ManageBookPort bookPort;

    @Override
    @Transactional
    public void enrichMetadata() {

        List<Book> bookList = bookPort.retrieveTop25BookMetadataStatus(List.of(MetadataStatus.PENDING, MetadataStatus.FAILED));
        if (bookList == null || bookList.isEmpty()) {
            return;
        }

        log.info("Starting background metadata sweep for {} books with PENDING or FAILED status", bookList.size());

        // links each book to the exact request made for it
        Map<Book, ApiMetadataSearchRequest> bookToRequest = new HashMap<>();
        for (Book book : bookList) {
            bookToRequest.put(book, new ApiMetadataSearchRequest(book.getLocalTitle(), book.getLocalAuthors(), null));
        }

        // Fetch metadata — adapter returns Map<ApiMetadataSearchRequest, ApiMetadataResponse>
        Map<ApiMetadataSearchRequest, ApiMetadataResponse> requestToMetadata = metadataPort.fetchBookMetadata(
                new ArrayList<>(bookToRequest.values())
        );
        if (requestToMetadata == null || requestToMetadata.isEmpty()) {
            return;
        }

        for (Book book : bookList) {
            ApiMetadataResponse metadata = requestToMetadata.get(bookToRequest.get(book));

            if (metadata == null) {
                book.setMetadataStatus(MetadataStatus.FAILED);
                continue;
            }

            if (metadata.authors() != null && !metadata.authors().isEmpty()) {
                book.setApiAuthors(metadata.authors());
            }

            if (metadata.title() != null && !metadata.title().isBlank()) {
                book.setApiTitle(metadata.title());
            }

            if (metadata.description() != null && !metadata.description().isBlank()) {
                book.setSynopsis(metadata.description());
            }

            if (metadata.genres() != null && !metadata.genres().isEmpty()) {
                book.setApiGenres(metadata.genres());
            }

            if (metadata.releaseYear() != null) {
                book.setApiReleaseYear(metadata.releaseYear());
            }

            if (metadata.coverPath() != null && !metadata.coverPath().isBlank()) {
                String downloadedCover = downloadCoverPort.downloadCover(metadata.coverPath());
                book.setCoverPath(downloadedCover);
            }

            book.setMetadataStatus(MetadataStatus.COMPLETE);
        }

        bookPort.updateBookMetadata(bookList);
    }
}