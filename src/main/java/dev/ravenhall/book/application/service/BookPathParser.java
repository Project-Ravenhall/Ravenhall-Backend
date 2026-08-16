package dev.ravenhall.book.application.service;

import dev.ravenhall.book.adapters.in.scanner.file.pdfbox.PdfExtractor;
import dev.ravenhall.book.application.ports.in.ManageBookPaths;
import dev.ravenhall.book.application.ports.in.ManageBookPort;
import dev.ravenhall.book.application.ports.in.commands.UploadBookCommand;
import dev.ravenhall.book.application.ports.out.queries.LocalMetadata;
import dev.ravenhall.book.domain.models.TextNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookPathParser implements ManageBookPaths {

    private final PdfExtractor pdfExtractor;
    private final ManageBookPort manageBookPort;

    // there are 3 possibilities here, either the book is in root folder
    // or in an author folder, in that either in series folder or not series
    // so Author/Series/Book/book.pdf or Author/Book/book.pdf or book.pdf
    @Override
    public void parseBookPath(Path absolutePath, Path relativePath) {

        Optional<LocalMetadata> metadata = pdfExtractor.extract(absolutePath.toFile());
        if (metadata.isEmpty()) {
            log.error("Book Extracted Local Metadata is empty: {}", absolutePath);
            return;
        }

        UploadBookCommand.UploadBookCommandBuilder builder = UploadBookCommand.builder()
                .fileFormat(metadata.get().fileFormat())
                .filePath(metadata.get().filePath())
                .fileChecksum(metadata.get().fileChecksum())
                .pageCount(metadata.get().pageCount())
                .fileSize(metadata.get().fileSize());

        int depth = relativePath.getNameCount();

        UploadBookCommand command = switch (depth) {
//            case 1 -> builder // book.pdf 0 (Not Supported Yet...)
//                .localTitle(getTitle(relativePath, 0))
//                .localAuthors(List.of("Unknown Author"))
//                .build();

            case 3 -> builder // Author/Book/book.pdf  0/1/2
                .localTitle(getTitle(relativePath, 1))
                .localAuthors(getAuthors(relativePath, 0))
                .build();

            case 4 -> builder // Author/Series/Book/book.pdf  0/1/2/3
                .localTitle(getTitle(relativePath, 2))
                .series(getSeries(relativePath, 1))
                .localAuthors(getAuthors(relativePath, 0))
                .build();

            default -> {
                log.info("Invalid directory depth ({}). Path violates structure rules and will be skipped: {}", depth, relativePath);
                yield null;
            }
        };

        if (command != null) {
            manageBookPort.ingestBook(command);
        }
    }

    private List<String> getAuthors(Path bookPath, int index) {
        String authorsString = bookPath.getName(index).toString();
        return Arrays.stream(authorsString.split(","))
                .map(TextNormalizer::normalize)
                .sorted()
                .toList();
    }

    private String getSeries(Path bookPath, int index) {
        return TextNormalizer.normalize(bookPath.getName(index).toString());
    }

    private String getTitle(Path bookPath, int index) {
        return TextNormalizer.normalize(bookPath.getName(index).toString());
    }
}