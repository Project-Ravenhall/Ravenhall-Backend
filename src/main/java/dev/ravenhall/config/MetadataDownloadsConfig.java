package dev.ravenhall.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class MetadataDownloadsConfig {

    private final Path rootLocation;
    private final Path bookCover;

    public MetadataDownloadsConfig(@Value("${metadata.storage.path}") String rootLocation,
                                   @Value("${metadata.storage.covers-dir}") String bookCover)
    {
        this.rootLocation = Paths.get(rootLocation);
        this.bookCover = Paths.get(bookCover);
    }

    @PostConstruct
    public void init() {
        try {
            // createDirectories checks if directory exists first, createDirectory does not
            // this single line will create all directories since they are all subdirectories
            Files.createDirectories(rootLocation.resolve(bookCover));
            log.info("book-cover Directory Created: {}", rootLocation.resolve(bookCover).toAbsolutePath());
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
            throw new RuntimeException("Could not initialize storage", e);
        }
    }
}
