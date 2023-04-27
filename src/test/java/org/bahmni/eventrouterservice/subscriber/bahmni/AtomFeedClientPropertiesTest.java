package org.bahmni.eventrouterservice.subscriber.bahmni;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtomFeedClientPropertiesTest {

    @Test
    public void giveValidAuthURL_whenGettingBaseURL_thenReturnBaseUrlFromAuthURI() {
        String authURI = "http://openmrs:8080/openmrs/ws/rest/v1/session";

        AtomFeedClientProperties properties = new AtomFeedClientProperties();
        properties.setAuthUri(authURI);
        properties.setUser("user");
        properties.setPassword("password");
        properties.setConnectionTimeoutInMilliseconds(3000);
        properties.setReplyTimeoutInMilliseconds(3000);
        properties.setMaxFailedEvents(2);

        assertEquals("http://openmrs:8080", properties.baseURL());
    }

    @Test
    public void giveInValidAuthURL_whenGettingBaseURL_thenThrowRuntimeException() {
        String authURI = "openmrs:8080/openmrs/ws/rest/v1/session";

        AtomFeedClientProperties properties = new AtomFeedClientProperties();
        properties.setAuthUri(authURI);
        properties.setUser("user");
        properties.setPassword("password");
        properties.setConnectionTimeoutInMilliseconds(3000);
        properties.setReplyTimeoutInMilliseconds(3000);
        properties.setMaxFailedEvents(2);

        RuntimeException exception = assertThrows(RuntimeException.class, properties::baseURL);
        assertEquals("Is not a valid URL - "+authURI, exception.getMessage());
    }
}