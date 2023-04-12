package org.bahmni.eventrouterservice.publisher.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FailedToPublishExceptionTest {

    @Test
    public void givenDestinationEitherAsTopicNameOrURLAndUnderlyingException_whenCreatingException_thenCreateExceptionMessageInExpectedFormat() {
        RuntimeException underlyingException = new RuntimeException("some error");
        FailedToPublishException exception = new FailedToPublishException("destination-topic", underlyingException);

        assertEquals("Failed to publish payload on destination-topic", exception.getMessage());
        assertEquals(underlyingException, exception.getCause());
    }

    @Test
    public void givenDestinationEitherAsTopicNameOrURL_whenCreatingException_thenCreateExceptionMessageInExpectedFormat() {
        FailedToPublishException exception = new FailedToPublishException("destination-topic");

        assertEquals("Failed to publish payload on destination-topic", exception.getMessage());
    }
}