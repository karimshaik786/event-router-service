package org.bahmni.eventrouterservice.atomfeed.jobs;

import lombok.AllArgsConstructor;
import org.bahmni.eventrouterservice.atomfeed.client.AtomFeedClientFactory;
import org.bahmni.eventrouterservice.atomfeed.worker.PatientFeedWorker;
import org.bahmni.eventrouterservice.config.BahmniFeedProperties;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class FeedProcessor {
    private final BahmniFeedProperties bahmniFeedProperties;
    private final PatientFeedWorker patientFeedWorker;
    private final AtomFeedClientFactory atomFeedClientFactory;

    public void processPatientFeed() {
        FeedClient atomFeedClient = atomFeedClientFactory.get(bahmniFeedProperties.getOpenmrs().getPatientFeedUri(),
                patientFeedWorker);
        atomFeedClient.processEvents();
    }

    public void processPatientFeedFailedEvents() {
        FeedClient atomFeedClient = atomFeedClientFactory.get(bahmniFeedProperties.getOpenmrs().getPatientFeedUri(),
                patientFeedWorker);
        atomFeedClient.processFailedEvents();
    }
}
