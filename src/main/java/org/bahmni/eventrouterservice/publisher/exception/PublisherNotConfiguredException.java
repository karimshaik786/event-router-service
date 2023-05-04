package org.bahmni.eventrouterservice.publisher.exception;

public class PublisherNotConfiguredException extends RuntimeException {
    public PublisherNotConfiguredException(String publisherId) {
        super("Publisher "+publisherId+" not configured");
    }
}
