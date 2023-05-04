package org.bahmni.eventrouterservice.subscriber.exception;

public class FailedToSubscribeException extends RuntimeException {
    public FailedToSubscribeException(Exception exception) {
        super("Failed to subscribe events", exception);
    }
}
