package org.bahmni.eventrouterservice.publisher.gcp;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.bahmni.eventrouterservice.publisher.common.exception.FailedToPublishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ConditionalOnProperty(
        value = {"spring.cloud.gcp.pubsub.enabled"},
        havingValue = "true"
)
@Component
public class GCPEventPublisher {
    Logger logger = LoggerFactory.getLogger(GCPEventPublisher.class);
    private final PubSubTemplate pubSubTemplate;

    @Autowired
    public GCPEventPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void publish(String topicName, String payload) {
        try {
            pubSubTemplate.publish(topicName, payload).get();
        } catch (InterruptedException | ExecutionException | CancellationException exception ) {
            logger.error("Failed to publish the payload : "+payload+" on topic : "+topicName);
            throw new FailedToPublishException(topicName, exception);
        }
    }
}
