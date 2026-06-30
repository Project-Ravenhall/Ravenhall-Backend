package dev.ravenhall.book.adapters.in.file.filewatcher;

import dev.ravenhall.book.adapters.in.file.pdfbox.PdfExtractor;
import dev.ravenhall.book.application.ports.in.ManageBookUseCase;
import dev.ravenhall.book.application.ports.in.commands.UploadBookCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookFileIngestionAdapter {

    private final PdfExtractor pdfExtractor;
    private final ManageBookUseCase manageBookUseCase;

    @ServiceActivator(inputChannel = "fileInputChannel")
    public void processDiscoveredFile(Message<File> message) {
        File pdfFile = message.getPayload();

        log.info("Watcher discovered a new PDF: {}", pdfFile.getName());

        try {
            UploadBookCommand command = pdfExtractor.extract(pdfFile);
            manageBookUseCase.ingestBook(command);

        }
        catch (IOException e) {
            log.error("Error processing PDF file: {}", pdfFile.getName(), e);
        }
    }
}