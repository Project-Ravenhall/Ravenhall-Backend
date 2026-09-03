package dev.ravenhall.book.adapters.in.scheduler;

import dev.ravenhall.book.application.ports.in.ManageMetadataUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetadataSweep {

    private final ManageMetadataUseCase manageMetadataUseCase;

    @Scheduled(fixedRate = 60000)
    public void sweep() {
        try {
            manageMetadataUseCase.enrichMetadata();
        }
        catch (Exception ex) {
            log.error("Book Metadata Sweep Error: ", ex);
        }
    }
}
