package org.bahmni.eventrouterservice.publisher.common.exception;

public class PublisherNotConfiguredException extends RuntimeException {
    public PublisherNotConfiguredException(String publisherId) {
        super("Publisher "+publisherId+" not configured");
    }
}
