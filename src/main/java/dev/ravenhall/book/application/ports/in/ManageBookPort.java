package dev.ravenhall.book.application.ports.in;

import dev.ravenhall.book.application.ports.in.commands.UploadBookCommand;
import dev.ravenhall.book.domain.Book;

import java.util.List;
import java.util.UUID;

public interface ManageBookPort {

    void ingestBook(UploadBookCommand bookCommand);
    Book retrieveBook(UUID id);
    List<Book> retrieveAllBooks();
}
