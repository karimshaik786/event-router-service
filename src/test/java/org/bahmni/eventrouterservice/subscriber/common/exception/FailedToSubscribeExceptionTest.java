package org.bahmni.eventrouterservice.subscriber.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FailedToSubscribeExceptionTest {

    @Test
    public void givenRootCauseOfException_whenCreatingException_thenCreateExceptionMessageInExpectedFormat() {
        FailedToSubscribeException exception = new FailedToSubscribeException(new RuntimeException("exception"));

        assertEquals("Failed to subscribe events", exception.getMessage());
    }
}