package dev.ravenhall.book.application.service;

import dev.ravenhall.book.application.ports.in.ManageBookUseCase;
import dev.ravenhall.book.application.ports.in.data.book.UploadBookCommand;
import dev.ravenhall.book.application.ports.in.data.progress.ProgressUpdate;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import dev.ravenhall.book.domain.Progress;
import dev.ravenhall.book.domain.enums.MetadataStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageBookService implements ManageBookUseCase {

    private final dev.ravenhall.book.application.ports.out.ManageBookPort manageBookPort;
    private final Clock clock;

    @Override
    @Transactional
    public void ingestBook(UploadBookCommand bookCommand) {

        UUID seriesId = null;
        if (bookCommand.series() != null && !bookCommand.series().isBlank()) {
            seriesId = manageBookPort.upsertSeries(bookCommand.series(), bookCommand.localAuthors());
        }

        Book book = new Book(bookCommand.localTitle(), clock, bookCommand.localAuthors(), seriesId, MetadataStatus.PENDING);
        BookAsset newAsset = new BookAsset(
                UUID.randomUUID(),
                bookCommand.fileFormat(),
                bookCommand.filePath(),
                bookCommand.pageCount(),
                bookCommand.fileChecksum(),
                bookCommand.fileSize()
        );
        book.addAsset(newAsset);
        manageBookPort.storeBook(book);
    }

    @Override
    @Transactional
    public Book retrieveBook(UUID id, UUID userId) {

        Book book = manageBookPort.retrieveBook(id);
        List<UUID> assetIds =  book.getAssets().stream().map(BookAsset::getId).toList();

        Map<UUID, Integer> progressMap = getProgressForAssets(userId, assetIds);

        book.getAssets().forEach(asset -> {
            asset.setPage(progressMap.getOrDefault(asset.getId(), 0));
        });

        return book;
    }

    @Override
    public List<Book> retrieveAllBooks() {
        return manageBookPort.retrieveAllBooks();
    }

    @Override
    @Transactional
    public BookAsset retrieveAsset(UUID assetId) {
        return manageBookPort.retrieveAsset(assetId);
    }

    public Map<UUID, Integer> getProgressForAssets(UUID userId, List<UUID> assetIds) {
        return manageBookPort.getProgressForAssets(userId, assetIds);
    }

    @Override
    @Transactional
    public Progress updateProgress(ProgressUpdate update) {
        log.debug("Received progress update for user {} on asset {}: page {}, version {}", 
                update.userId(), update.assetId(), update.latestPage(), update.version());
        Progress progressDomain = new Progress(update.userId(), update.assetId(), update.latestPage(), update.version());
        return manageBookPort.updateProgress(progressDomain);
    }

    @Override
    public Progress getProgressForAsset(UUID userId, UUID assetId) {
        return manageBookPort.getProgressForAsset(userId, assetId);
    }
}
