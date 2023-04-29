package org.bahmni.eventrouterservice.subscriber.gcp;

import org.bahmni.eventrouterservice.model.ServiceName;
import org.bahmni.eventrouterservice.publisher.service.EventPublisherService;
import org.bahmni.eventrouterservice.publisher.service.EventPublisherServiceFactory;
import org.bahmni.eventrouterservice.subscriber.exception.FailedToSubscribeException;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration.SubscriberDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(
        value = {"spring.cloud.gcp.pubsub.enabled"},
        havingValue = "true"
)
@Component
public class GCPEventSubscriber implements Runnable {
    Logger logger = LoggerFactory.getLogger(GCPEventSubscriber.class);
    private final PubSubTemplate pubSubTemplate;

    private final SubscriberConfiguration subscriberConfiguration;

    private final EventPublisherServiceFactory eventPublisherServiceFactory;

    @Autowired
    public GCPEventSubscriber(PubSubTemplate pubSubTemplate,
                              EventPublisherServiceFactory eventPublisherServiceFactory,
                              SubscriberConfiguration subscriberConfiguration)
    {
        this.pubSubTemplate = pubSubTemplate;
        this.eventPublisherServiceFactory = eventPublisherServiceFactory;
        this.subscriberConfiguration = subscriberConfiguration;
    }

    @Override
    public void run() {
        logger.info("GCP Subscriber job started...");
        List<SubscriberDescription> subscribersDescription = subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(ServiceName.GCP);

        try {
            for(SubscriberDescription subscriber: subscribersDescription) {
                List<AcknowledgeablePubsubMessage> messages = pubSubTemplate.pull(subscriber.getSubscriptionId(), subscriber.maxMessages(), false);
                logger.info("Total messages consumed : "+messages.size()+" for subscription id  :"+subscriber.getSubscriptionId());
                if(!messages.isEmpty())
                    process(subscriber, messages);
            }

            logger.info("GCP Subscriber Job Finished.");
        } catch (Exception exception) {
            logger.error("GCP Subscriber Job Termination with cause : " + exception.getMessage());
            throw new FailedToSubscribeException(exception);
        }
    }

    private void process(SubscriberDescription subscriber, List<AcknowledgeablePubsubMessage> messages) {
        EventPublisherService publisherService = eventPublisherServiceFactory.getById(subscriber.getPublisherId());
        for (AcknowledgeablePubsubMessage ackPubsubMessage: messages) {
            String payloadAsString = ackPubsubMessage.getPubsubMessage().getData().toStringUtf8();
            logger.debug("Message consumed for subscription id : "+subscriber.getSubscriptionId() + " and payload as : "+payloadAsString);
            try {
                publisherService.publish(payloadAsString, subscriber.getPublisherId());
                ackPubsubMessage.ack();
            } catch (Exception exception) {
                logger.error("Failed to publish events for subscriber : "+ subscriber.getSubscriptionId() + " and message id : " + ackPubsubMessage.getPubsubMessage().getMessageId());
                ackPubsubMessage.nack();
                throw exception;
            }
        }
        logger.info("Total messages published : "+messages.size()+" for subscription id  :"+subscriber.getSubscriptionId());
    }
}
