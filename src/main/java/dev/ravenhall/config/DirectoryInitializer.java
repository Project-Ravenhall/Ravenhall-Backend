package dev.ravenhall.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
public class DirectoryInitializer {

    @Value("${directory.books:data/media/books}")
    private String booksDir;

    @Value("${metadata.path:data/media/metadata}")
    private String metadataDir;

    @Value("${metadata.images.books.book-covers:data/media/metadata/images/books/book-covers}")
    private String coversDir;

    @Value("${jwt.secret.storage.path:/data/secrets/jwt.secret}")
    private String jwtSecretPath;

    @PostConstruct
    public void initDirectories() {
        log.info("Initializing essential application directories...");

        List<String> directoriesToCreate = List.of(
                booksDir,
                metadataDir,
                coversDir
        );

        for (String dirPath : directoriesToCreate) {
            try {
                Path path = Paths.get(dirPath);
                
                // If it's a relative coversDir (e.g., just "images/books/book-covers"), 
                // we should probably resolve it against metadataDir like the adapter does.
                // But the adapter handles that and creates it on demand anyway.
                // This ensures the explicitly provided paths are created.
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                    log.info("Created directory: {}", path.toAbsolutePath());
                }
            } catch (IOException e) {
                log.error("Failed to create directory: {}", dirPath, e);
            }
        }

        // Also ensure the parent directory for the JWT secret exists
        try {
            Path jwtPath = Paths.get(jwtSecretPath).getParent();
            if (jwtPath != null && !Files.exists(jwtPath)) {
                Files.createDirectories(jwtPath);
                log.info("Created JWT secret directory: {}", jwtPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create JWT secret directory", e);
        }
    }
}
