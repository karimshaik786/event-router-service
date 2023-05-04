package org.bahmni.eventrouterservice.publisher.exception;

public class EndpointNotConfiguredForPublisherException extends RuntimeException {
    public EndpointNotConfiguredForPublisherException(String publisherId) {
        super("Endpoint not configured for publisher id : "+publisherId);
    }
}
