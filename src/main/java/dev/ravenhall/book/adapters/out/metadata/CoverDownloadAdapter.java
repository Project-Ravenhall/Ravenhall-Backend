package dev.ravenhall.book.adapters.out.metadata;

import dev.ravenhall.book.application.ports.out.metadata.DownloadBookCoverPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoverDownloadAdapter implements DownloadBookCoverPort {

    private final RestClient restClient;

    @Value("${metadata.path}")
    private String rootLocationStr;

    @Value("${metadata.images.books.book-covers}")
    private String coversDirStr;

    @Override
    @Retryable(backoff = @Backoff(delay = 3000))
    public String downloadCover(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url is null or empty");
        }

        byte[] imageBytes = restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_OCTET_STREAM) // Use octet-stream for raw bytes
                .retrieve()
                .body(byte[].class);

        return storeImage(imageBytes);
    }

    private String storeImage(byte[] imageBytes) {
        try {
            Path rootLocation = Paths.get(rootLocationStr);
            Path coversDir = Paths.get(coversDirStr);

            Path finalDir;
            if (coversDir.isAbsolute() || coversDir.startsWith(rootLocation)) {
                finalDir = coversDir;
            } else {
                finalDir = rootLocation.resolve(coversDir);
            }

            Files.createDirectories(finalDir);

            String fileName = UUID.randomUUID() + ".jpg";
            Path targetFilePath = finalDir.resolve(fileName);
            Files.write(targetFilePath, imageBytes);

            return targetFilePath.toAbsolutePath().toString();

        } catch (IOException e) {
            log.error("Error while saving downloaded cover image to disk", e);
            // must throw a RuntimeException here so the transaction fails
            // and the Retryable annotation knows something went wrong
            throw new RuntimeException("Failed to store cover image", e);
        }
    }
}