package org.bahmni.eventrouterservice.subscriber.gcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.bahmni.eventrouterservice.publisher.bahmni.BahmniEventPublisherService;
import org.bahmni.eventrouterservice.publisher.service.EventPublisherServiceFactory;
import org.bahmni.eventrouterservice.subscriber.exception.FailedToSubscribeException;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.bahmni.eventrouterservice.model.ServiceName.GCP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GCPEventSubscriberTest {

    @Mock
    private PubSubTemplate pubSubTemplate;

    @Mock
    private SubscriberConfiguration subscriberConfiguration;

    @Mock
    private EventPublisherServiceFactory eventPublisherServiceFactory;

    private GCPEventSubscriber gcpEventSubscriber;

    @BeforeEach
    public void setup() {
        gcpEventSubscriber = new GCPEventSubscriber(pubSubTemplate, eventPublisherServiceFactory, subscriberConfiguration);
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyGetTheListOfSubscriptionInOrder() {
        when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(GCP)).thenReturn(Collections.emptyList());

        gcpEventSubscriber.run();

        Mockito.verify(subscriberConfiguration, Mockito.times(1)).getSubscribersAsPerOrderOfSubscriptionFor(GCP);
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenFetchRecordsForEachSubscription() {

        when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(GCP)).thenReturn(defaultDescriptions());
        when(pubSubTemplate.pull("test-topic-tw-sub", 20, false)).thenReturn(Mockito.anyList());

        gcpEventSubscriber.run();

        Mockito.verify(pubSubTemplate, Mockito.times(1)).pull("test-topic-tw-sub", 20, false);
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyGetThePublisherToPublishEvents() {

        String payload = "{\"hospital\": \"Zambia\", \"uuid\": \"03f8124e-b636-468a-b5b0-7a95545f5bd5\", \"kid\": \"True\"}";
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();

        AcknowledgeablePubsubMessage acknowledgeablePubsubMessage = mock(AcknowledgeablePubsubMessage.class);

        List<AcknowledgeablePubsubMessage> mockMessages = List.of(acknowledgeablePubsubMessage);
        when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(GCP)).thenReturn(defaultDescriptions());
        when(eventPublisherServiceFactory.getById("bahmni-patient-kid")).thenReturn(mock(BahmniEventPublisherService.class));
        when(pubSubTemplate.pull("test-topic-tw-sub", 20, false)).thenReturn(mockMessages);
        when(acknowledgeablePubsubMessage.getPubsubMessage()).thenReturn(pubsubMessage);

        gcpEventSubscriber.run();

        Mockito.verify(eventPublisherServiceFactory, Mockito.times(1)).getById("bahmni-patient-kid");
    }

    @Test
    public void givenScheduledOfSubscribingAndNoMessagesToSubscribe_whenSubscribing_thenNotPublishAnyEvents() {

        when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(GCP)).thenReturn(defaultDescriptions());
        when(pubSubTemplate.pull("test-topic-tw-sub", 20, false)).thenReturn(Mockito.anyList());

        gcpEventSubscriber.run();

        Mockito.verify(eventPublisherServiceFactory, Mockito.times(0)).getById("bahmni-patient-kid");
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyPublishTheMessageToPublisher() {

        String payload = "{\"hospital\": \"Zambia\", \"uuid\": \"03f8124e-b636-468a-b5b0-7a95545f5bd5\", \"kid\": \"True\"}";
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();

        AcknowledgeablePubsubMessage acknowledgeablePubsubMessage = mock(AcknowledgeablePubsubMessage.class);
        BahmniEventPublisherService mockPublisher = mock(BahmniEventPublisherService.class);

        List<AcknowledgeablePubsubMessage> mockMessages = List.of(acknowledgeablePubsubMessage);
        when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(GCP)).thenReturn(defaultDescriptions());
        when(eventPublisherServiceFactory.getById("bahmni-patient-kid")).thenReturn(mockPublisher);
        when(pubSubTemplate.pull("test-topic-tw-sub", 20, false)).thenReturn(mockMessages);
        when(acknowledgeablePubsubMessage.getPubsubMessage()).thenReturn(pubsubMessage);
        doNothing().when(mockPublisher).publish(payload, "bahmni-patient-kid");

        gcpEventSubscriber.run();

        Mockito.verify(mockPublisher, Mockito.times(1)).publish(payload, "bahmni-patient-kid");
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyAcknowledgePublishedMessages() {

        String payload = "{\"hospital\": \"Zambia\", \"uuid\": \"03f8124e-b636-468a-b5b0-7a95545f5bd5\", \"kid\": \"True\"}";
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();

        AcknowledgeablePubsubMessage mockMessage = mock(AcknowledgeablePubsubMessage.class);
        BahmniEventPublisherService mockPublisher = mock(BahmniEventPublisherService.class);

        List<AcknowledgeablePubsubMessage> mockMessages = List.of(mockMessage);
        when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(GCP)).thenReturn(defaultDescriptions());
        when(eventPublisherServiceFactory.getById("bahmni-patient-kid")).thenReturn(mockPublisher);
        when(pubSubTemplate.pull("test-topic-tw-sub", 20, false)).thenReturn(mockMessages);
        when(mockMessage.getPubsubMessage()).thenReturn(pubsubMessage);
        doNothing().when(mockPublisher).publish(payload, "bahmni-patient-kid");
        when(mockMessage.ack()).thenReturn(new CompletableFuture<>());

        gcpEventSubscriber.run();

        Mockito.verify(mockMessage, Mockito.times(1)).ack();
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenSuccessfullyNotAcknowledgeForMessagesFailedToPublish() {

        String payload = "{\"hospital\": \"Zambia\", \"uuid\": \"03f8124e-b636-468a-b5b0-7a95545f5bd5\", \"kid\": \"True\"}";
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();

        AcknowledgeablePubsubMessage mockMessage = mock(AcknowledgeablePubsubMessage.class);
        BahmniEventPublisherService mockPublisher = mock(BahmniEventPublisherService.class);

        List<AcknowledgeablePubsubMessage> mockMessages = List.of(mockMessage);
        when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(GCP)).thenReturn(defaultDescriptions());
        when(eventPublisherServiceFactory.getById("bahmni-patient-kid")).thenReturn(mockPublisher);
        when(pubSubTemplate.pull("test-topic-tw-sub", 20, false)).thenReturn(mockMessages);
        when(mockMessage.getPubsubMessage()).thenReturn(pubsubMessage);
        doThrow(new RuntimeException()).when(mockPublisher).publish(payload, "bahmni-patient-kid");
        when(mockMessage.nack()).thenReturn(new CompletableFuture<>());

        assertThrows(FailedToSubscribeException.class, () -> gcpEventSubscriber.run());

        Mockito.verify(mockMessage, Mockito.times(1)).nack();
    }

    @Test
    public void givenScheduledOfSubscribing_whenSubscribing_thenThrowExceptionForMessagesFailedToPublish() {

        String payload = "{\"hospital\": \"Zambia\", \"uuid\": \"03f8124e-b636-468a-b5b0-7a95545f5bd5\", \"kid\": \"True\"}";
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();

        AcknowledgeablePubsubMessage mockMessage = mock(AcknowledgeablePubsubMessage.class);
        BahmniEventPublisherService mockPublisher = mock(BahmniEventPublisherService.class);

        List<AcknowledgeablePubsubMessage> mockMessages = List.of(mockMessage);
        when(subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(GCP)).thenReturn(defaultDescriptions());
        when(eventPublisherServiceFactory.getById("bahmni-patient-kid")).thenReturn(mockPublisher);
        when(pubSubTemplate.pull("test-topic-tw-sub", 20, false)).thenReturn(mockMessages);
        when(mockMessage.getPubsubMessage()).thenReturn(pubsubMessage);
        doThrow(new RuntimeException()).when(mockPublisher).publish(payload, "bahmni-patient-kid");
        when(mockMessage.nack()).thenReturn(new CompletableFuture<>());

        FailedToSubscribeException exception = assertThrows(FailedToSubscribeException.class, () -> gcpEventSubscriber.run());

        assertEquals("Failed to subscribe events", exception.getMessage());
    }

    private List<SubscriberConfiguration.SubscriberDescription> defaultDescriptions() {
        try {
            String subscriberDescriptionConfigurationAsJson = """
                    [
                       {
                            "source": {
                              "serviceName": "GCP",
                              "topic": {
                                "subscriptionId": "test-topic-tw-sub",
                                "maxMessages": 20
                              }
                            },
                            "publisherId": "bahmni-patient-kid"
                      }
                    ]""";
            return new ObjectMapper().readValue(subscriberDescriptionConfigurationAsJson, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}