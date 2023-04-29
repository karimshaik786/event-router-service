package org.bahmni.eventrouterservice.publisher.service;

public interface EventPublisherService {
    void publish(String payload, String publisherId);
}
