package org.bahmni.eventrouterservice.publisher;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
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
public class GCPEventPublisher {
    Logger logger = LoggerFactory.getLogger(GCPEventPublisher.class);
    private final PubSubTemplate pubSubTemplate;

    @Autowired
    public GCPEventPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void publish(String topicName, String payload) {
        pubSubTemplate.publish(topicName, payload);
        logger.info("Successfully publish the payload on topic "+topicName);
    }
}
