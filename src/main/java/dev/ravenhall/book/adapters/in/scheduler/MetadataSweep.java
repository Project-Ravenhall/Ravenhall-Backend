package dev.ravenhall.book.adapters.in.scheduler;

import dev.ravenhall.book.application.ports.in.ManageMetadataUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetadataSweep {

    private final ManageMetadataUseCase manageMetadataUseCase;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong lastRun = new AtomicLong(0);
    private final long COUNTDOWN = 5 * 60 * 1000;

    @Scheduled(initialDelay = 60_000, fixedRate = COUNTDOWN)
    public void autoSweep() {
        boolean acquired = running.compareAndSet(false, true);
        if (!acquired) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        boolean cooldownExpired = (currentTime - lastRun.get()) >= COUNTDOWN;

        if (!cooldownExpired) {
            running.set(false);
            return;
        }

        try {
            log.info("Book Metadata Automatic Sweep Started");
            manageMetadataUseCase.enrichMetadata();
            lastRun.set(System.currentTimeMillis());
        } catch (Exception ex) {
            log.error("Book Metadata Automatic Sweep Error: ", ex);
        } finally {
            running.set(false);
        }
    }

    public void manualSweep() {
        boolean acquired = running.compareAndSet(false, true);
        if (!acquired) {
            throw new RuntimeException("Book Metadata Sweep Already Running");
        }

        try {
            log.info("Book Metadata Manual Sweep Started");
            manageMetadataUseCase.enrichMetadata();
            lastRun.set(System.currentTimeMillis());
        } catch (Exception ex) {
            log.error("Book Metadata Manual Sweep Error: ", ex);
        } finally {
            running.set(false);
        }
    }

}
