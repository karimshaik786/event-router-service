package org.bahmni.eventrouterservice.publisher.gcp;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.bahmni.eventrouterservice.publisher.common.exception.FailedToPublishException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GCPEventPublisherTest {

    @Mock
    private PubSubTemplate pubSubTemplate;

    @InjectMocks
    private GCPEventPublisher gcpEventPublisher;

    @Test
    public void givenTopicNameAndPayload_whenPublishing_thenSuccessfullyPublishPayloadToTopic() {
        String topicName = "topic-name";
        String payload = """
                {
                    "data": "payload"
                }
                """;

        Mockito.when(pubSubTemplate.publish(topicName, payload)).thenReturn(CompletableFuture.completedFuture("success"));

        gcpEventPublisher.publish(topicName, payload);

        Mockito.verify(pubSubTemplate, Mockito.times(1)).publish(topicName, payload);
    }

    @Test
    public void givenTopicNameAndPayload_whenPublishing_thenThrowCancellationException() {
        String topicName = "topic-name";
        String payload = """
                {
                    "data": "payload"
                }
                """;

        Mockito.when(pubSubTemplate.publish(topicName, payload)).thenReturn(CompletableFuture.failedFuture(new CancellationException()));

        FailedToPublishException exception = assertThrows(FailedToPublishException.class, () -> gcpEventPublisher.publish(topicName, payload));

        assertEquals("Failed to publish payload on topic-name", exception.getMessage());
    }

    @Test
    public void givenTopicNameAndPayload_whenPublishing_thenThrowInterruptedException() {
        String topicName = "topic-name";
        String payload = """
                {
                    "data": "payload"
                }
                """;

        Mockito.when(pubSubTemplate.publish(topicName, payload)).thenReturn(CompletableFuture.failedFuture(new InterruptedException()));

        FailedToPublishException exception = assertThrows(FailedToPublishException.class, () -> gcpEventPublisher.publish(topicName, payload));

        assertEquals("Failed to publish payload on topic-name", exception.getMessage());
    }

    @Test
    public void givenTopicNameAndPayload_whenPublishing_thenThrowExecutionException() {
        String topicName = "topic-name";
        String payload = """
                {
                    "data": "payload"
                }
                """;

        Mockito.when(pubSubTemplate.publish(topicName, payload)).thenReturn(CompletableFuture.failedFuture(new ExecutionException(new RuntimeException())));

        FailedToPublishException exception = assertThrows(FailedToPublishException.class, () -> gcpEventPublisher.publish(topicName, payload));

        assertEquals("Failed to publish payload on topic-name", exception.getMessage());
    }
}