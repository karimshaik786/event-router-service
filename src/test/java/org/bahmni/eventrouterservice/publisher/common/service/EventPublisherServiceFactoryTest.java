package org.bahmni.eventrouterservice.publisher.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.eventrouterservice.publisher.bahmni.BahmniEventPublisherService;
import org.bahmni.eventrouterservice.publisher.common.exception.PublisherNotConfiguredException;
import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration.PublisherDescription;
import org.bahmni.eventrouterservice.publisher.gcp.GCPEventPublisherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventPublisherServiceFactoryTest {
    @Mock
    private PublisherConfiguration publisherConfiguration;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private BahmniEventPublisherService bahmniEventPublisherService;
    @Mock
    private GCPEventPublisherService gcpEventPublisherService;


    @Test
    public void givenBahmniPublisherId_whenGettingPublisherService_thenReturnBhamniPublisherService() {
        Mockito.when(publisherConfiguration.getPublisherDescriptions()).thenReturn(defaultBahmniDescriptions());
        Mockito.when(applicationContext.getBean(BahmniEventPublisherService.class)).thenReturn(bahmniEventPublisherService);

        EventPublisherServiceFactory eventPublisherServiceFactory = new EventPublisherServiceFactory(publisherConfiguration, applicationContext);

        EventPublisherService publisherService = eventPublisherServiceFactory.getById("bahmni-patient-kid");

        assertEquals(bahmniEventPublisherService, publisherService);
    }

    @Test
    public void givenGCPPublisherId_whenGettingPublisherService_thenReturnGCPPublisherService() {
        Mockito.when(publisherConfiguration.getPublisherDescriptions()).thenReturn(defaultGCPDescriptions());
        Mockito.when(applicationContext.getBean(GCPEventPublisherService.class)).thenReturn(gcpEventPublisherService);

        EventPublisherServiceFactory eventPublisherServiceFactory = new EventPublisherServiceFactory(publisherConfiguration, applicationContext);

        EventPublisherService publisherService = eventPublisherServiceFactory.getById("gcp-patient-registration");

        assertEquals(gcpEventPublisherService, publisherService);
    }

    @Test
    public void givenGCPPublisherIdIsNotConfigured_whenGettingPublisherService_thenThrowsPublisherNotConfiguredException() {
        Mockito.when(publisherConfiguration.getPublisherDescriptions()).thenReturn(emptyList());

        EventPublisherServiceFactory eventPublisherServiceFactory = new EventPublisherServiceFactory(publisherConfiguration, applicationContext);

        PublisherNotConfiguredException exception = assertThrows(PublisherNotConfiguredException.class, () -> {
            eventPublisherServiceFactory.getById("gcp-patient-registration");
        });

        assertEquals("Publisher gcp-patient-registration not configured", exception.getMessage());
    }

    private List<PublisherDescription> defaultBahmniDescriptions() {
        try {
            String publisherDescriptionConfigurationAsJson = """
                    [
                      {
                        "id": "bahmni-patient-kid",
                        "destination": {
                          "serviceName": "BAHMNI",
                          "endpoint": "http://localhost:3000/publish"
                        }
                      }
                    ]""";
            return new ObjectMapper().readValue(publisherDescriptionConfigurationAsJson, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<PublisherDescription> defaultGCPDescriptions() {
        try {
            String publisherDescriptionConfigurationAsJson = """
                    [
                      {
                        "id": "gcp-patient-registration",
                        "destination": {
                          "serviceName": "GCP",
                          "topic": {
                            "name": "gcp-test-topic"
                          }
                        }
                      }
                    ]""";
            return new ObjectMapper().readValue(publisherDescriptionConfigurationAsJson, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}