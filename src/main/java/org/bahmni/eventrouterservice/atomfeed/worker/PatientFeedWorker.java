package org.bahmni.eventrouterservice.atomfeed.worker;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bahmni.eventrouterservice.config.BahmniFeedProperties;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@AllArgsConstructor
@Component
public class PatientFeedWorker implements EventWorker {
    private final HttpClient openmrsHttpClient;

    private final BahmniFeedProperties feedProperties;

    @Override
    public void process(Event event) {
        try {
            log.info("Getting patient details ...");
            String patientUri = event.getContent();
            String patientFR = getPatientFeedRecord(patientUri);
            log.info(patientFR);
        } catch (Exception e) {
            log.error("Failed to fetch patient details", e);
            throw new RuntimeException("Failed to fetch patient details", e);
        }
    }

    @Override
    public void cleanUp(Event event) {
    }


    public String getPatientFeedRecord(String patientUrl) {
        return openmrsHttpClient.get(URI.create(feedProperties.getOpenmrs().getAuthority() + patientUrl));
    }
}
