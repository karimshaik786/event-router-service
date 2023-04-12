package org.bahmni.eventrouterservice.publisher.bahmni;

import org.bahmni.eventrouterservice.publisher.common.exception.FailedToPublishException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.POST;

@ExtendWith(MockitoExtension.class)
class BahmniEventPublisherTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BahmniEventPublisher bahmniEventPublisher;

    @Test
    public void givenURLAndPayload_whenPublishing_thenSuccessfullyPublishPayloadToURL() {
        String url = "http://demo.bahmni.org/patientreg";
        String payload = "payload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatusCode.valueOf(200));

        Mockito.when(restTemplate.exchange(url, POST, request, String.class)).thenReturn(responseEntity);

        bahmniEventPublisher.publish(url, payload);

        Mockito.verify(restTemplate, Mockito.times(1)).exchange(url, POST, request, String.class);
    }

    @Test
    public void givenURLAndPayload_whenPublishing_thenThrowFailedToPublishExceptionForNon200ResponseStatus() {
        String url = "http://demo.bahmni.org/patientreg";
        String payload = "payload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatusCode.valueOf(300));

        Mockito.when(restTemplate.exchange(url, POST, request, String.class)).thenReturn(responseEntity);

        FailedToPublishException exception = assertThrows(FailedToPublishException.class, () -> bahmniEventPublisher.publish(url, payload));

        assertEquals("Failed to publish payload on http://demo.bahmni.org/patientreg", exception.getMessage());
    }

    @Test
    public void givenURLAndPayload_whenPublishing_thenThrowFailedToPublishExceptionForRuntimeException() {
        String url = "http://demo.bahmni.org/patientreg";
        String payload = "payload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        Mockito.when(restTemplate.exchange(url, POST, request, String.class)).thenThrow(new RuntimeException("exception"));

        FailedToPublishException exception = assertThrows(FailedToPublishException.class, () -> bahmniEventPublisher.publish(url, payload));

        assertEquals("Failed to publish payload on http://demo.bahmni.org/patientreg", exception.getMessage());
    }
}