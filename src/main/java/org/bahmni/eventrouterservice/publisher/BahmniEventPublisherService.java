package org.bahmni.eventrouterservice.publisher;

import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BahmniEventPublisherService implements EventPublisherService {
    private final BahmniEventPublisher eventPublisher;
    private final PublisherConfiguration publisherConfiguration;

    @Autowired
    public BahmniEventPublisherService(BahmniEventPublisher eventPublisher, PublisherConfiguration publisherConfiguration) {
        this.eventPublisher = eventPublisher;
        this.publisherConfiguration = publisherConfiguration;
    }

    @Override
    public void publish(String payload, String publisherId) {
        String endpoint = publisherConfiguration.getEndpointFor(publisherId).get();
        eventPublisher.publish(endpoint, payload);
    }
}
