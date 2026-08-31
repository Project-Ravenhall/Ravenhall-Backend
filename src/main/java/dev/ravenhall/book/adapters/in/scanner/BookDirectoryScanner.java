package dev.ravenhall.book.adapters.in.scanner;

import dev.ravenhall.book.application.ports.in.ManageBookPathsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookDirectoryScanner {

    @Value("${directory.books}")
    private String bookRootDirectoryString;

    private final List<String> fileFormats = List.of(".pdf", ".epub");
    private final ManageBookPathsUseCase manageBookPathsUseCase;

    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        log.info("Application started. Running initial library scan...");
        scan();
        log.info("Initial library scan complete.");
    }
    @Scheduled(fixedDelay = 900000)
    public void runScheduledScan() {
        scan();
    }

    public void scan() {
        Path bookRootDirectory = Paths.get(bookRootDirectoryString);

        try {
            Files.walkFileTree(bookRootDirectory, new SimpleFileVisitor<>() {

                @Override
                @NullMarked
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String fileName = file.getFileName().toString();
                    if (fileFormats.stream().anyMatch(fileName::endsWith)) {
                        Path relativePath = bookRootDirectory.relativize(file);
                        manageBookPathsUseCase.parseBookPath(file, relativePath);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                @NullMarked
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    try (Stream<Path> files = Files.list(dir)) {
                        List<Path> directoryContents = files.toList();

                        boolean isBookFolder = directoryContents.stream()
                                .filter(Files::isRegularFile)
                                .anyMatch(path -> {
                                    String name = path.getFileName().toString();
                                    return fileFormats.stream().anyMatch(name::endsWith);
                                });

                        if (isBookFolder) {
                            if (dir.equals(bookRootDirectory)) {
                                // if root folder has files we continue and let visitFile scan them
                                return FileVisitResult.CONTINUE;
                            } else {
                                // if it's a books folder then we scan each file before skipping
                                directoryContents.stream()
                                        .filter(Files::isRegularFile)
                                        .filter(path -> {
                                            String name = path.getFileName().toString();
                                            return fileFormats.stream().anyMatch(name::endsWith);
                                        })
                                        .forEach(file -> {
                                            Path relativePath = bookRootDirectory.relativize(file);
                                            manageBookPathsUseCase.parseBookPath(file, relativePath);
                                        });
                                return FileVisitResult.SKIP_SUBTREE;
                            }
                        }
                    } catch (IOException e) {
                        log.error("Error while reading files from directory {}", dir, e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                @NullMarked
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    log.error("Error processing file: {}", file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("Directory Parsing Error: {}", bookRootDirectory, e);
        }
    }
}