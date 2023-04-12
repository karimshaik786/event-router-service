package org.bahmni.eventrouterservice.publisher.gcp;

import org.bahmni.eventrouterservice.configuration.Topic;
import org.bahmni.eventrouterservice.publisher.bahmni.BahmniEventPublisherService;
import org.bahmni.eventrouterservice.publisher.common.service.EventPublisherService;
import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
        value = {"spring.cloud.gcp.pubsub.enabled"},
        havingValue = "true"
)
@Component
public class GCPEventPublisherService implements EventPublisherService {
    Logger logger = LoggerFactory.getLogger(GCPEventPublisherService.class);
    private final GCPEventPublisher eventPublisher;
    private final PublisherConfiguration publisherConfiguration;

    @Autowired
    public GCPEventPublisherService(GCPEventPublisher eventPublisher, PublisherConfiguration publisherConfiguration) {
        this.eventPublisher = eventPublisher;
        this.publisherConfiguration = publisherConfiguration;
    }

    @Override
    public void publish(String payload, String publisherId) {
        Topic topic = publisherConfiguration.getTopicFor(publisherId);
        eventPublisher.publish(topic.getName(), payload);
        logger.debug("Successfully publish the message on topic :" + topic.getName() + " with payload " + payload);
    }
}
