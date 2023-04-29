package org.bahmni.eventrouterservice.publisher.exception;

public class TopicNotConfiguredForPublisherException extends RuntimeException {
    public TopicNotConfiguredForPublisherException(String publisherId) {
        super("Topic not configured for publisher id : "+publisherId);
    }
}
