package org.bahmni.eventrouterservice.publisher;

import org.bahmni.eventrouterservice.Topic;
import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
        value = {"spring.cloud.gcp.pubsub.enabled"},
        havingValue = "true"
)
@Component
public class GCPEventPublisherService implements EventPublisherService {
    private final GCPEventPublisher eventPublisher;
    private final PublisherConfiguration publisherConfiguration;

    @Autowired
    public GCPEventPublisherService(GCPEventPublisher eventPublisher, PublisherConfiguration publisherConfiguration) {
        this.eventPublisher = eventPublisher;
        this.publisherConfiguration = publisherConfiguration;
    }

    @Override
    public void publish(String payload, String publisherId) {
        Topic topic = publisherConfiguration.getTopicFor(publisherId).get();
        eventPublisher.publish(topic.getName(), payload);
    }
}
