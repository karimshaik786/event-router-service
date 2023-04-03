package org.bahmni.eventrouterservice.publisher;

public interface EventPublisherService {
    void publish(String payload, String publisherId);
}
