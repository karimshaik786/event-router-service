package org.bahmni.eventrouterservice.publisher.bahmni;

import org.bahmni.eventrouterservice.publisher.common.service.EventPublisherService;
import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BahmniEventPublisherService implements EventPublisherService {

    Logger logger = LoggerFactory.getLogger(BahmniEventPublisherService.class);
    private final BahmniEventPublisher eventPublisher;
    private final PublisherConfiguration publisherConfiguration;

    @Autowired
    public BahmniEventPublisherService(BahmniEventPublisher eventPublisher, PublisherConfiguration publisherConfiguration) {
        this.eventPublisher = eventPublisher;
        this.publisherConfiguration = publisherConfiguration;
    }

    @Override
    public void publish(String payload, String publisherId) {
        String endpoint = publisherConfiguration.getEndpointFor(publisherId);
        eventPublisher.publish(endpoint, payload);
        logger.info("Successfully publish the message on url :" + endpoint);
        logger.debug("Successfully publish the message on url :" + endpoint + " with payload " + payload);
    }
}
