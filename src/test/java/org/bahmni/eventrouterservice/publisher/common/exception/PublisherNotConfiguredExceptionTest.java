package org.bahmni.eventrouterservice.publisher.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PublisherNotConfiguredExceptionTest {

    @Test
    public void givenPublisherId_whenCreatingException_thenCreateExceptionMessageInExpectedFormat() {
        PublisherNotConfiguredException exception = new PublisherNotConfiguredException("publisher-id");

        assertEquals("Publisher publisher-id not configured", exception.getMessage());
    }
}