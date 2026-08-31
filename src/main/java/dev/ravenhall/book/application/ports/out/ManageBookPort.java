package dev.ravenhall.book.application.ports.out;

import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import dev.ravenhall.book.domain.Progress;
import dev.ravenhall.book.domain.enums.MetadataStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ManageBookPort {
    void storeBook(Book book);
    void updateBookMetadata(List<Book> books);
    UUID upsertSeries(String seriesName, List<String> authors);
    Book retrieveBook(UUID id);
    List<Book> retrieveAllBooks();
    List<Book> retrieveTop25BookMetadataStatus(List<MetadataStatus> statuses);
    BookAsset retrieveAsset(UUID assetId);
    Map<UUID, Integer> getProgressForAssets(UUID userId, List<UUID> assetIds);
    Progress getProgressForAsset(UUID userId, UUID assetId);
    Progress updateProgress(Progress progress);


}
