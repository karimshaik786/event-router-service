package org.bahmni.eventrouterservice.publisher.service;

import org.bahmni.eventrouterservice.publisher.bahmni.BahmniEventPublisherService;
import org.bahmni.eventrouterservice.publisher.exception.PublisherNotConfiguredException;
import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.bahmni.eventrouterservice.publisher.gcp.GCPEventPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
        initializedPublishers();
    }

    private void initializedPublishers() {
        publisherConfiguration.getPublisherDescriptions().forEach(publisherDescription -> {
            switch (publisherDescription.getServiceName()) {
                case BAHMNI -> publisherIdToPublisherService.put(publisherDescription.getId(),
                        applicationContext.getBean(BahmniEventPublisherService.class));
                case GCP -> publisherIdToPublisherService.put(publisherDescription.getId(),
                        applicationContext.getBean(GCPEventPublisherService.class));
            }
        });
    }

    public EventPublisherService getById(String publisherId) {
        if(!publisherIdToPublisherService.containsKey(publisherId))
            throw new PublisherNotConfiguredException(publisherId);
        return publisherIdToPublisherService.get(publisherId);
    }
}
