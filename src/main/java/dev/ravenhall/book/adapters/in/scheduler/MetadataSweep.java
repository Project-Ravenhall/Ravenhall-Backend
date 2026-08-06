package dev.ravenhall.book.adapters.in.scheduler;

import dev.ravenhall.book.application.ports.in.ManageMetadataPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetadataSweep {

    private final ManageMetadataPort manageMetadataPort;

    @Scheduled(fixedRate = 60000)
    public void sweep() {
        manageMetadataPort.enrichMetadata();
        log.info("Book Metadata sweep started");
    }
}
