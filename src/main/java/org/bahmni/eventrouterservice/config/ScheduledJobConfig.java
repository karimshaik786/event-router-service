package org.bahmni.eventrouterservice.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bahmni.eventrouterservice.atomfeed.jobs.FeedProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ScheduledJobConfig {
    private final FeedProcessor feedProcessor;

    @Scheduled(fixedDelay = 2000, initialDelay = 3000)
    public void scheduler() {
        log.info("process the feed...");
        feedProcessor.processPatientFeed();
    }
}
