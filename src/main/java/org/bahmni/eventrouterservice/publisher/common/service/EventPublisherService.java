package org.bahmni.eventrouterservice.publisher.common.service;

public interface EventPublisherService {
    void publish(String payload, String publisherId);
}
