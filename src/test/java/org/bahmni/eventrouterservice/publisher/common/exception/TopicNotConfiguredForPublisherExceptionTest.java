package org.bahmni.eventrouterservice.publisher.common.exception;

import org.bahmni.eventrouterservice.publisher.exception.TopicNotConfiguredForPublisherException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TopicNotConfiguredForPublisherExceptionTest {

    @Test
    public void givenPublisherId_whenCreatingException_thenCreateExceptionMessageInExpectedFormat() {
        TopicNotConfiguredForPublisherException exception = new TopicNotConfiguredForPublisherException("1");

        assertEquals("Topic not configured for publisher id : 1", exception.getMessage());
    }
}