package org.bahmni.eventrouterservice.subscriber.bahmni;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.eventrouterservice.publisher.common.exception.PublisherNotConfiguredException;
import org.bahmni.eventrouterservice.publisher.common.service.EventPublisherServiceFactory;
import org.bahmni.eventrouterservice.publisher.gcp.GCPEventPublisherService;
import org.bahmni.eventrouterservice.subscriber.common.exception.FailedToSubscribeException;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.bahmni.eventrouterservice.configuration.ServiceName.BAHMNI;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BahmniEventSubscriberTest {

    @Mock
    private SubscriberConfiguration subscriberConfiguration;
    @Mock
    private EventPublisherServiceFactory eventPublisherServiceFactory;
    @Mock
    private RestTemplate restTemplate;
    private BahmniEventSubscriber bahmniEventSubscriber;

    @BeforeEach
    public void setup() {
        bahmniEventSubscriber = new BahmniEventSubscriber(subscriberConfiguration, eventPublisherServiceFactory, restTemplate, new ObjectMapper());
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyGetTheListOfSubscriptionInOrder() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(Collections.emptyList());

        bahmniEventSubscriber.run();

        Mockito.verify(subscriberConfiguration, Mockito.times(1)).getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI);
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyFetchRecordsForEachSubscription() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(defaultDescriptions());
        Mockito.when(restTemplate.getForObject("https://gorest.co.in/public/v2/users",  List.class)).thenReturn(Collections.emptyList());

        bahmniEventSubscriber.run();

        Mockito.verify(restTemplate, Mockito.times(1)).getForObject("https://gorest.co.in/public/v2/users", List.class);
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyGetThePublisherToPublishEvents() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(defaultDescriptions());
        Mockito.when(eventPublisherServiceFactory.getById("gcp-patient-registration"))
                .thenReturn(mock(GCPEventPublisherService.class));
        Mockito.when(restTemplate.getForObject("https://gorest.co.in/public/v2/users",  List.class)).thenReturn(defaultResponse());

        bahmniEventSubscriber.run();

        Mockito.verify(eventPublisherServiceFactory, Mockito.times(1)).getById("gcp-patient-registration");
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyPublishEvents() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(defaultDescriptions());
        GCPEventPublisherService publisherService = mock(GCPEventPublisherService.class);
        Mockito.when(eventPublisherServiceFactory.getById("gcp-patient-registration"))
                .thenReturn(publisherService);
        Mockito.when(restTemplate.getForObject("https://gorest.co.in/public/v2/users",  List.class)).thenReturn(defaultResponse());

        bahmniEventSubscriber.run();

        String payload = "{\"id\":904244,\"name\":\"Dwaipayan Johar\",\"email\":\"dwaipayan_johar@erdman.name\",\"gender\":\"female\",\"status\":\"inactive\"}";

        Mockito.verify(publisherService, Mockito.times(defaultDescriptions().size())).publish(payload, "gcp-patient-registration");
    }

    @Test
    public void givenScheduledOfSubscribingAndNoRecordsFoundAgainstSubscription_whenSubscribing_thenDoNotPublishEvents() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(defaultDescriptions());
        GCPEventPublisherService publisherService = mock(GCPEventPublisherService.class);
        Mockito.when(restTemplate.getForObject("https://gorest.co.in/public/v2/users",  List.class)).thenReturn(emptyResponse());

        bahmniEventSubscriber.run();

        Mockito.verify(publisherService, Mockito.times(0)).publish(any(), any());
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenFailedToSubscribeEvents() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(defaultDescriptions());
        Mockito.when(restTemplate.getForObject("https://gorest.co.in/public/v2/users",  List.class)).thenThrow(new RuntimeException());

        assertThrows(FailedToSubscribeException.class, () -> bahmniEventSubscriber.run());
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenFailedToGetPublisher() {
        Mockito.when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(BAHMNI)).thenReturn(defaultDescriptions());
        Mockito.when(eventPublisherServiceFactory.getById("gcp-patient-registration"))
                .thenThrow(new PublisherNotConfiguredException("gcp-patient-registration"));
        Mockito.when(restTemplate.getForObject("https://gorest.co.in/public/v2/users",  List.class)).thenReturn(defaultResponse());

        assertThrows(FailedToSubscribeException.class, () -> bahmniEventSubscriber.run());
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

    private List defaultResponse() {
       String json = """
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
        try {
            return new ObjectMapper().readValue(json, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List emptyResponse() {
        String json = """
              []
           """;
        try {
            return new ObjectMapper().readValue(json, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}