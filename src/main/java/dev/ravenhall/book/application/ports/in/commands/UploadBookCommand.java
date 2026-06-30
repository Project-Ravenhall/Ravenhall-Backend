package dev.ravenhall.book.application.ports.in.commands;

import dev.ravenhall.book.domain.enums.FileFormat;

public record UploadBookCommand (
        String title,
        String author,
        FileFormat fileFormat,
        String filePath,
        String fileChecksum,
        int pageCount,
        long fileSize
) {

}