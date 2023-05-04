package org.bahmni.eventrouterservice.subscriber.bahmni;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bahmni.eventrouterservice.model.ServiceName;
import org.bahmni.eventrouterservice.publisher.service.EventPublisherService;
import org.bahmni.eventrouterservice.publisher.service.EventPublisherServiceFactory;
import org.bahmni.eventrouterservice.subscriber.exception.FailedToSubscribeException;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration.SubscriberDescription;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BahmniEventSubscriber implements Runnable {

        private final SubscriberConfiguration subscriberConfiguration;
        private final EventPublisherServiceFactory eventPublisherServiceFactory;
        private final HttpClient bahmniHttpClient;
        private final AtomFeedClientProperties feedProperties;
        private final AtomFeedClientFactory atomFeedClientFactory;

    @Override
    public void run() {
        try {
            log.info("Bhamni Subscriber job started...");

            List<SubscriberDescription> subscriberDescriptions = subscriberConfiguration
                    .getSubscribersAsPerOrderOfSubscriptionFor(ServiceName.BAHMNI);
            for(SubscriberDescription subscriber: subscriberDescriptions) {

                EventPublisherService publisherService = eventPublisherServiceFactory.getById(subscriber.getPublisherId());
                Worker newWorker = new Worker(bahmniHttpClient,
                        feedProperties.getBaseUrl(),
                        publisherService,
                        subscriber.getPublisherId());

                FeedClient atomFeedClient = atomFeedClientFactory.get(subscriber.getEndpoint(), newWorker);
                atomFeedClient.processEvents();
            }

            log.info("Bhamni Subscriber Job Finished.");
        } catch (Exception exception) {
            log.error("Bhamni Subscriber Job Termination with cause : " + exception.getMessage());
            throw new FailedToSubscribeException(exception);
        }
    }
    @Slf4j
    @AllArgsConstructor
    public static class Worker implements EventWorker {
        private final HttpClient bahmniHttpClient;
        private final String baseURL;
        private final EventPublisherService publisherService;
        private final String publisherId;

        @Override
        public void process(Event event) {
            log.info("Getting patient details ...");

            URI patientContentURI = URI.create(baseURL + event.getContent());
            String patientFeed = bahmniHttpClient.get(patientContentURI);

            log.info("Successfully received the payload from bahmni to publish : " + patientFeed);

            publisherService.publish(patientFeed, publisherId);
        }

        @Override
        public void cleanUp(Event event) {}
    }
}