package dev.ravenhall.book.adapters.in.scanner.file.exceptions;

import lombok.Getter;

import java.io.IOException;

@Getter
public class BookIOException extends IOException {

    public BookIOException(String message) {
        super(message);
    } // not used, so far

}
