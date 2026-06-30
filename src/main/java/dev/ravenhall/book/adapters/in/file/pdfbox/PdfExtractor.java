package dev.ravenhall.book.adapters.in.file.pdfbox;

import dev.ravenhall.book.application.ports.in.commands.UploadBookCommand;
import dev.ravenhall.book.domain.enums.FileFormat;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class PdfExtractor {

    public UploadBookCommand extract(File pdfFile) throws IOException {
        try (PDDocument pdf = Loader.loadPDF(pdfFile))
        {
            PDDocumentInformation pdi = pdf.getDocumentInformation();
            String author = pdi.getAuthor() != null ? pdi.getAuthor() : "Unknown";
            String title = pdi.getTitle() != null ? pdi.getTitle() : pdfFile.getName(); // Simply gets tile or just takes the file name (whatever it is).
            String filepath = pdfFile.getAbsolutePath();
            long size = pdfFile.length();
            int pageCount = pdf.getNumberOfPages();

            String checksum = "some checksum";

            FileFormat fileFormat = FileFormat.PDF;

            return new UploadBookCommand(
                    title,
                    author,
                    fileFormat,
                    filepath,
                    checksum,
                    pageCount,
                    size
            );
        }
    }
}
