package dev.ravenhall.book.adapters.in.scanner.file.pdfbox;

import dev.ravenhall.book.application.ports.out.data.metadata.local.LocalMetadataExtract;
import dev.ravenhall.book.domain.enums.FileFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Component
@Slf4j
public class PdfExtractor {

    public Optional<LocalMetadataExtract> extract(File pdfFile) {
        try (PDDocument pdf = Loader.loadPDF(pdfFile)) {

            String filepath = pdfFile.getAbsolutePath();
            long size = pdfFile.length();
            int pageCount = pdf.getNumberOfPages();
            String checksum = sha256(pdfFile);

            return Optional.of(new LocalMetadataExtract(
                    FileFormat.PDF,
                    filepath,
                    checksum,
                    pageCount,
                    size
            ));
        }
        catch (IOException e) {
            log.error("Failed to extract file {}: {}", pdfFile.getAbsolutePath(), e.getMessage());
            return Optional.empty();
        }
    }


    private static String sha256(File file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(Files.readAllBytes(file.toPath()));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed available on every JVM, so this is unreachable in practice.
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}