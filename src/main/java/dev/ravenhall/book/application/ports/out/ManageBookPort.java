package dev.ravenhall.book.application.ports.out;

import dev.ravenhall.book.domain.Book;

import java.util.UUID;

public interface ManageBookPort {
    void storeBook(Book book);
    Book retrieveBook(UUID id);

}
