package org.bahmni.eventrouterservice.publisher.common.exception;

import org.bahmni.eventrouterservice.publisher.exception.EndpointNotConfiguredForPublisherException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndpointNotConfiguredForPublisherExceptionTest {

    @Test
    public void givenPublisherId_whenCreatingException_thenCreateExceptionMessageInExpectedFormat() {
        EndpointNotConfiguredForPublisherException exception = new EndpointNotConfiguredForPublisherException("1");

        assertEquals("Endpoint not configured for publisher id : 1", exception.getMessage());
    }
}