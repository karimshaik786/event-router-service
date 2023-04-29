package org.bahmni.eventrouterservice.subscriber.bahmni;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.eventrouterservice.publisher.exception.PublisherNotConfiguredException;
import org.bahmni.eventrouterservice.publisher.service.EventPublisherServiceFactory;
import org.bahmni.eventrouterservice.publisher.gcp.GCPEventPublisherService;
import org.bahmni.eventrouterservice.subscriber.exception.FailedToSubscribeException;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.bahmni.eventrouterservice.model.ServiceName.BAHMNI;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BahmniEventSubscriberTest {

    @Mock
    private SubscriberConfiguration subscriberConfiguration;
    @Mock
    private EventPublisherServiceFactory eventPublisherServiceFactory;
    @Mock
    private HttpClient bahmniHttpClient;
    @Mock
    private AtomFeedClientProperties feedProperties;
    @Mock
    private AtomFeedClientFactory atomFeedClientFactory;
    private BahmniEventSubscriber bahmniEventSubscriber;

    @BeforeEach
    public void setup() {
        bahmniEventSubscriber = new BahmniEventSubscriber(subscriberConfiguration, eventPublisherServiceFactory, bahmniHttpClient, feedProperties, atomFeedClientFactory);
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyGetTheListOfSubscriptionInOrder() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(Collections.emptyList());

        bahmniEventSubscriber.run();

        Mockito.verify(subscriberConfiguration, Mockito.times(1)).getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI);
    }


    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyGetThePublisherToPublishEvents() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(defaultDescriptions());
        Mockito.when(eventPublisherServiceFactory.getById("gcp-patient-registration"))
                .thenReturn(mock(GCPEventPublisherService.class));
        Mockito.when(atomFeedClientFactory.get(any(), any())).thenReturn(mock(FeedClient.class));

        bahmniEventSubscriber.run();

        Mockito.verify(eventPublisherServiceFactory, Mockito.times(1)).getById("gcp-patient-registration");
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyInvokeProcessEvents() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(defaultDescriptions());
        Mockito.when(eventPublisherServiceFactory.getById("gcp-patient-registration"))
                .thenReturn(mock(GCPEventPublisherService.class));
        FeedClient feedClient = mock(FeedClient.class);
        Mockito.when(atomFeedClientFactory.get(any(), any())).thenReturn(feedClient);

        bahmniEventSubscriber.run();

        Mockito.verify(eventPublisherServiceFactory, Mockito.times(1)).getById("gcp-patient-registration");

        Mockito.verify(feedClient, Mockito.times(defaultDescriptions().size())).processEvents();
    }


    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenFailedToGetPublisher() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(defaultDescriptions());
        Mockito.when(eventPublisherServiceFactory.getById("gcp-patient-registration"))
                .thenThrow(new PublisherNotConfiguredException("gcp-patient-registration"));

        assertThrows(FailedToSubscribeException.class, () -> bahmniEventSubscriber.run());
    }


    @Test
    public void givenEvent_whenInvokingProcessEventOnWorker_thenGetAndPublishPatientFeed() {
        GCPEventPublisherService publisherService = mock(GCPEventPublisherService.class);
        String publisherId = "gcp-patient-registration";
        String baseURL = "http://openmrs:8080";
        String feedURI = "/openmrs/ws/rest/v1/patient/bf379289-62b7-47b3-ab7b-5186cb6fc46c?v=full";
        BahmniEventSubscriber.Worker worker = new BahmniEventSubscriber.Worker(bahmniHttpClient, baseURL, publisherService, publisherId);

        Mockito.when(bahmniHttpClient.get(URI.create(baseURL+feedURI))).thenReturn(defaultResponse());
        Mockito.doNothing().when(publisherService).publish(defaultResponse(), publisherId);

        Event tempEvent = new Event("eventId", feedURI);
        worker.process(tempEvent);

        verify(bahmniHttpClient, times(1)).get(URI.create(baseURL+feedURI));
        verify(publisherService, times(1)).publish(defaultResponse(), publisherId);
    }

    @Test
    public void givenEventAndGetPatientFeedIsThrowingError_whenInvokingProcessEventOnWorker_thenShouldNotPublishPatientFeed() {
        GCPEventPublisherService publisherService = mock(GCPEventPublisherService.class);
        String publisherId = "gcp-patient-registration";
        String baseURL = "http://openmrs:8080";
        String feedURI = "/openmrs/ws/rest/v1/patient/bf379289-62b7-47b3-ab7b-5186cb6fc46c?v=full";
        BahmniEventSubscriber.Worker worker = new BahmniEventSubscriber.Worker(bahmniHttpClient, baseURL, publisherService, publisherId);

        Mockito.when(bahmniHttpClient.get(URI.create(baseURL+feedURI))).thenThrow(new RuntimeException());

        Event tempEvent = new Event("eventId", feedURI);
        try {
            worker.process(tempEvent);
        }catch (RuntimeException e) {}

        verify(bahmniHttpClient, times(1)).get(URI.create(baseURL+feedURI));
        verify(publisherService, times(0)).publish(defaultResponse(), publisherId);
    }

    private List<SubscriberConfiguration.SubscriberDescription> defaultDescriptions() {
        try {
            String subscriberDescriptionConfigurationAsJson = """
                    [
                       {
                         "source": {
                           "serviceName": "BAHMNI",
                           "endpoint": "https://gorest.co.in/public/v2/users"
                         },
                         "orderOfSubscription": 1,
                         "publisherId": "gcp-patient-registration"
                       }
                     ]""";
            return new ObjectMapper().readValue(subscriberDescriptionConfigurationAsJson, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String defaultResponse() {
        return """
               [
                 {
                   "id": 904244,
                   "name": "Dwaipayan Johar",
                   "email": "dwaipayan_johar@erdman.name",
                   "gender": "female",
                   "status": "inactive"
                 }
             ]
           """;
    }
}