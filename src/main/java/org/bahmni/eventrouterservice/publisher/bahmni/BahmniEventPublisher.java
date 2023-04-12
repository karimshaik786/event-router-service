package org.bahmni.eventrouterservice.publisher.bahmni;

import org.bahmni.eventrouterservice.publisher.common.exception.FailedToPublishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BahmniEventPublisher {
    Logger logger = LoggerFactory.getLogger(BahmniEventPublisher.class);
    private final RestTemplate restTemplate;

    @Autowired
    public BahmniEventPublisher(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void publish(String url, String payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if(!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new FailedToPublishException(url);
            }
        } catch (RuntimeException exception) {
            logger.error("Failed to publish the payload on url : "+url);
            throw new FailedToPublishException(url, exception);
        }
    }
}