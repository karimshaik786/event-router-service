package org.bahmni.eventrouterservice.publisher.bahmni;

import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BahmniEventPublisherServiceTest {

    @Mock
    private BahmniEventPublisher eventPublisher;

    @Mock
    private PublisherConfiguration publisherConfiguration;

    @InjectMocks
    private BahmniEventPublisherService bahmniEventPublisherService;

    @Test
    public void givenPublisherIdAndPayload_whenPublishing_thenGetEndpointToPublish() {
        String payload = "payload";
        String publisherId = "publisher-id";
        String endpoint = "http://bahmni-service.org/endpoint";

        when(publisherConfiguration.getEndpointFor(publisherId)).thenReturn(endpoint);

        bahmniEventPublisherService.publish(payload, publisherId);

        verify(publisherConfiguration, times(1)).getEndpointFor(publisherId );
    }

    @Test
    public void givenPublisherIdAndPayloadAndEndpoint_whenPublishing_thenPublishPayloadAtEndpoint() {
        String payload = "payload";
        String publisherId = "publisher-id";
        String endpoint = "http://bahmni-service.org/endpoint";

        when(publisherConfiguration.getEndpointFor(publisherId)).thenReturn(endpoint);
        doNothing().when(eventPublisher).publish(endpoint, payload);

        bahmniEventPublisherService.publish(payload, publisherId);

        verify(eventPublisher, times(1)).publish(endpoint, payload );
    }
}