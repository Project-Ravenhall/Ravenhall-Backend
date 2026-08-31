package dev.ravenhall.book.application.ports.in;

import dev.ravenhall.book.application.ports.in.data.book.UploadBookCommand;
import dev.ravenhall.book.application.ports.in.data.progress.ProgressUpdate;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import dev.ravenhall.book.domain.Progress;

import java.util.List;
import java.util.UUID;

public interface ManageBookUseCase {

    void ingestBook(UploadBookCommand bookCommand);
    Book retrieveBook(UUID id, UUID userId);
    List<Book> retrieveAllBooks();
    BookAsset retrieveAsset(UUID assetId);
    Progress getProgressForAsset(UUID userId, UUID assetId);
    Progress updateProgress(ProgressUpdate update);
}
