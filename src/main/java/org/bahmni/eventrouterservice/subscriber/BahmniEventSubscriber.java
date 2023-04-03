package org.bahmni.eventrouterservice.subscriber;

import org.bahmni.eventrouterservice.ServiceName;
import org.bahmni.eventrouterservice.publisher.EventPublisherService;
import org.bahmni.eventrouterservice.publisher.EventPublisherServiceFactory;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfig;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class BahmniEventSubscriber {

    Logger logger = LoggerFactory.getLogger(BahmniEventSubscriber.class);

    private final SubscriberConfig subscriberConfig;
    private final EventPublisherServiceFactory eventPublisherServiceFactory;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public BahmniEventSubscriber(SubscriberConfig subscriberConfig,
                                 EventPublisherServiceFactory eventPublisherServiceFactory,
                                 RestTemplate restTemplate,
                                 ObjectMapper objectMapper) {
        this.subscriberConfig = subscriberConfig;
        this.eventPublisherServiceFactory = eventPublisherServiceFactory;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 13000)
    public void subscribe() {
        subscriberConfig
                .getSubscriberFor(ServiceName.BAHMNI)
                .forEach(subscriber -> {
                    EventPublisherService publisherService = eventPublisherServiceFactory.getPublisherById(subscriber.getPublisherId());
                    Patient[] patients = restTemplate.getForObject(subscriber.getEndpoint(), Patient[].class);

                    Arrays.stream(patients).forEach(patient -> {
                        try {
                            String patientDataAsString = objectMapper.writeValueAsString(patient);
                            logger.info("Successfully received the payload from bahmni to publish : "+patientDataAsString);
                            publisherService.publish(patientDataAsString, subscriber.getPublisherId());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });

                });
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class Patient {
        private Long id;
        private String name;
        private String email;
        private String gender;
        private String status;

        public Patient() {
        }
    }

}