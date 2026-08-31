package dev.ravenhall.book.application.ports.in;

import java.nio.file.Path;

public interface ManageBookPathsUseCase {
    void parseBookPath(Path absolutePath, Path relativePath);
}
