package org.bahmni.eventrouterservice.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

        restTemplate.postForObject(url, request, String.class);
        logger.info("Successfully publish the message on url :" + url + " with payload " + payload);
    }
}