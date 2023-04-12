package org.bahmni.eventrouterservice.subscriber.bahmni;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.eventrouterservice.configuration.ServiceName;
import org.bahmni.eventrouterservice.publisher.common.service.EventPublisherService;
import org.bahmni.eventrouterservice.publisher.common.service.EventPublisherServiceFactory;
import org.bahmni.eventrouterservice.subscriber.common.exception.FailedToSubscribeException;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration.SubscriberDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class BahmniEventSubscriber implements Runnable {

    Logger logger = LoggerFactory.getLogger(BahmniEventSubscriber.class);

        private final SubscriberConfiguration subscriberConfiguration;
        private final EventPublisherServiceFactory eventPublisherServiceFactory;
        private final RestTemplate restTemplate;

        private final ObjectMapper objectMapper;

    @Autowired
    public BahmniEventSubscriber(SubscriberConfiguration subscriberConfiguration,
                                 EventPublisherServiceFactory eventPublisherServiceFactory,
                                 RestTemplate restTemplate,
                                 ObjectMapper objectMapper) {
        this.subscriberConfiguration = subscriberConfiguration;
        this.eventPublisherServiceFactory = eventPublisherServiceFactory;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run() {
        try {
            logger.info("Bhamni Subscriber job started...");

            List<SubscriberDescription> subscriberDescriptions = subscriberConfiguration
                    .getSubscribersAsPerOrderOfSubscriptionFor(ServiceName.BAHMNI);
            for(SubscriberDescription subscriber: subscriberDescriptions) {

                List records = restTemplate.getForObject(subscriber.getEndpoint(), List.class);
                logger.info("Total records consumed : "+ records.size()+" from url : "+subscriber.getEndpoint());
                if(!records.isEmpty())
                    publish(records, subscriber);
                logger.info("Total records published : "+ records.size()+" fetched from url : "+subscriber.getEndpoint());
            }

            logger.info("Bhamni Subscriber Job Finished.");
        } catch (Exception exception) {
            logger.error("Bhamni Subscriber Job Termination with cause : " + exception.getMessage());
            throw new FailedToSubscribeException(exception);
        }
    }

    private void publish(List records, SubscriberDescription subscriber) throws JsonProcessingException {
        EventPublisherService publisherService = eventPublisherServiceFactory.getById(subscriber.getPublisherId());
        for(Object record :  records) {
            logger.debug("Successfully received the payload from bahmni to publish : " + record);
            String recordAsString = objectMapper.writeValueAsString(record);
            publisherService.publish(recordAsString, subscriber.getPublisherId());
        }
    }
}