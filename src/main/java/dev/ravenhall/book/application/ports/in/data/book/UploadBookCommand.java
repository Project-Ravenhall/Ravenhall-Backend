package dev.ravenhall.book.application.ports.in.data.book;

import dev.ravenhall.book.domain.enums.FileFormat;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record UploadBookCommand(
        String localTitle,
        List<String> localAuthors,
        String series,
        FileFormat fileFormat,
        String filePath,
        String assetName,
        String fileChecksum,
        int pageCount,
        long fileSize
) {}