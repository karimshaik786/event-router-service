package org.bahmni.eventrouterservice.subscriber;

import org.bahmni.eventrouterservice.ServiceName;
import org.bahmni.eventrouterservice.publisher.EventPublisherService;
import org.bahmni.eventrouterservice.publisher.EventPublisherServiceFactory;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfig;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(
        value = {"spring.cloud.gcp.pubsub.enabled"},
        havingValue = "true"
)
@Component
public class GCPEventSubscriber {
    Logger logger = LoggerFactory.getLogger(GCPEventSubscriber.class);
    private final PubSubTemplate pubSubTemplate;

    private final SubscriberConfig subscriberConfig;

    private final EventPublisherServiceFactory eventPublisherServiceFactory;

    @Autowired
    public GCPEventSubscriber(PubSubTemplate pubSubTemplate,
                              EventPublisherServiceFactory eventPublisherServiceFactory,
                              SubscriberConfig subscriberConfig)
    {
        this.pubSubTemplate = pubSubTemplate;
        this.eventPublisherServiceFactory = eventPublisherServiceFactory;
        this.subscriberConfig = subscriberConfig;
    }

    @Scheduled(fixedDelay = 30000)
    public void consume() {
        subscriberConfig
                .getSubscriberFor(ServiceName.GCP)
                .forEach(subscriber -> {
                    List<AcknowledgeablePubsubMessage> messages = pubSubTemplate.pull(subscriber.getSubscriptionId(), 2, false);
                    EventPublisherService publisherService = eventPublisherServiceFactory.getPublisherById(subscriber.getPublisherId());
                    messages.forEach(ackPubsubMessage -> {
                        String payload = ackPubsubMessage.getPubsubMessage().getData().toStringUtf8();
                        logger.info("Successfully consumed message :" + payload);
                        publisherService.publish(payload, subscriber.getPublisherId());
                        ackPubsubMessage.ack();
                    });
                });
    }
}
