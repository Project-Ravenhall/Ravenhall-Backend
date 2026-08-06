package dev.ravenhall.book.application.ports.out.queries;

import dev.ravenhall.book.domain.enums.FileFormat;

public record LocalMetadata(
        FileFormat fileFormat,
        String filePath,
        String fileChecksum,
        int pageCount,
        long fileSize
) {}
