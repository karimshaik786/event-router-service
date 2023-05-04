package org.bahmni.eventrouterservice.publisher.exception;

public class FailedToPublishException extends RuntimeException {
    public FailedToPublishException(String destination, Exception exception) {
        super("Failed to publish payload on " + destination, exception);
    }

    public FailedToPublishException(String destination) {
        super("Failed to publish payload on " + destination);
    }
}
