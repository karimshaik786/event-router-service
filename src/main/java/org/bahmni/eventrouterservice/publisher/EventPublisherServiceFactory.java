package org.bahmni.eventrouterservice.publisher;

import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventPublisherServiceFactory {

    private final PublisherConfiguration publisherConfiguration;
    private final ApplicationContext applicationContext;
    private final Map<String, EventPublisherService> publisherIdToPublisherService = new HashMap<>();

    @Autowired
    public EventPublisherServiceFactory(PublisherConfiguration publisherConfiguration, ApplicationContext applicationContext) {
        this.publisherConfiguration = publisherConfiguration;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void initializedPublishers() {
        publisherConfiguration.getPublisherDescriptions().forEach(publisherDescription -> {

            switch (publisherDescription.getServiceNameToPublish()) {
                case BAHMNI:
                    publisherIdToPublisherService.put(publisherDescription.getId(),
                            applicationContext.getBean(BahmniEventPublisherService.class));
                    break;
                case GCP:
                    publisherIdToPublisherService.put(publisherDescription.getId(),
                            applicationContext.getBean(GCPEventPublisherService.class));
                    break;
                default:
                    throw new PublisherNotSupportedException(publisherDescription.getServiceNameToPublish());
            }
        });
    }

    public EventPublisherService getPublisherById(String publisherId) {
        return publisherIdToPublisherService.get(publisherId);
    }
}
