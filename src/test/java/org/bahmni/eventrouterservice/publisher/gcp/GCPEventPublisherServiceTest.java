package org.bahmni.eventrouterservice.publisher.gcp;

import org.bahmni.eventrouterservice.configuration.Topic;
import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GCPEventPublisherServiceTest {
    @Mock
    private GCPEventPublisher eventPublisher;

    @Mock
    private PublisherConfiguration publisherConfiguration;

    @InjectMocks
    private GCPEventPublisherService gcpEventPublisherService;

    @Test
    public void givenPublisherIdAndPayload_whenPublishing_thenGetTopicToPublish() {
        String payload = "payload";
        String publisherId = "publisher-id";
        Topic topic = new Topic("topic-name");

        when(publisherConfiguration.getTopicFor(publisherId)).thenReturn(topic);

        gcpEventPublisherService.publish(payload, publisherId);

        verify(publisherConfiguration, times(1)).getTopicFor(publisherId);
    }

    @Test
    public void givenPublisherIdAndPayloadAndTopic_whenPublishing_thenPublishPayloadAtTopic() {
        String payload = "payload";
        String publisherId = "publisher-id";
        Topic topic = new Topic("topic-name");

        when(publisherConfiguration.getTopicFor(publisherId)).thenReturn(topic);
        doNothing().when(eventPublisher).publish("topic-name", payload);

        gcpEventPublisherService.publish(payload, publisherId);

        verify(eventPublisher, times(1)).publish("topic-name", payload );
    }
}