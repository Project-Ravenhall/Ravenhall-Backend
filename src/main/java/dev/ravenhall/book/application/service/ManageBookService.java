package dev.ravenhall.book.application.service;

import dev.ravenhall.book.application.ports.in.ManageBookPort;
import dev.ravenhall.book.application.ports.in.commands.UploadBookCommand;
import dev.ravenhall.book.domain.Book;
import dev.ravenhall.book.domain.BookAsset;
import dev.ravenhall.book.domain.enums.MetadataStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageBookService implements ManageBookPort {

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
    public Book retrieveBook(UUID id) {
        return manageBookPort.retrieveBook(id);
    }

    @Override
    public List<Book> retrieveAllBooks() {
        return manageBookPort.retrieveAllBooks();
    }
}
