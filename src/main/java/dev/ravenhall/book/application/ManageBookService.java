    package dev.ravenhall.book.application;

    import dev.ravenhall.book.application.ports.in.ManageBookUseCase;
    import dev.ravenhall.book.application.ports.in.commands.UploadBookCommand;
    import dev.ravenhall.book.application.ports.out.ManageBookPort;
    import dev.ravenhall.book.domain.Book;
    import dev.ravenhall.book.domain.BookAsset;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.Clock;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    public class ManageBookService implements ManageBookUseCase {

        private final ManageBookPort manageBookPort;
        private final Clock clock;

        @Override
        @Transactional
        public void ingestBook(UploadBookCommand bookCommand) {

            Book book = new Book(UUID.randomUUID(), bookCommand.title(), clock, bookCommand.author(), null);

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





    }
