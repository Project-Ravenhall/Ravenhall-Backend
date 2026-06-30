package dev.ravenhall.book.adapters.in.file.filewatcher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.inbound.FileReadingMessageSource;

import java.io.File;
import java.io.IOException;

@Configuration
public class FileWatcherConfig {

    @Value("${directory.books}")
    private String bookDirectory;

    @Bean
    @InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "5000"))
    public FileReadingMessageSource fileReadingMessageSource() {

        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(bookDirectory));

        CompositeFileListFilter<File> fileListFilter = new CompositeFileListFilter<>();
        fileListFilter.addFilter(new SimplePatternFileListFilter("*.pdf"));
        fileListFilter.addFilter(new AcceptOnceFileListFilter<>());

        source.setFilter(fileListFilter);

        return source;
    }
}