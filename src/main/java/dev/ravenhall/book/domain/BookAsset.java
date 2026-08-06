package dev.ravenhall.book.domain;

import dev.ravenhall.book.domain.enums.FileFormat;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BookAsset {

    private final UUID id;
    private final FileFormat fileFormat;
    private final String filepath;
    private final int pageCount;
    private final String checksum;
    private final long fileSize;

    public BookAsset(UUID id, FileFormat fileFormat, String filepath, int pageCount, String checksum, long fileSize) {
        if (filepath == null || filepath.isBlank()) {
            throw new IllegalArgumentException("Invalid filepath");
        }
        if (pageCount < 0 || fileSize <= 0) { // pageCount can technically be 0 if it's just an audio file later
            throw new IllegalArgumentException("Invalid file metrics");
        }

        this.id = (id != null) ? id : UUID.randomUUID();
        this.fileFormat = fileFormat;
        this.filepath = filepath;
        this.pageCount = pageCount;
        this.fileSize = fileSize;
        this.checksum = checksum;
    }
}