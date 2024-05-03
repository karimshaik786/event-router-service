package org.bahmni.eventrouterservice.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.Destination;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.RouteDescription;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.AdditionalProperty;
import org.bahmni.eventrouterservice.model.Topic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

import static org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.BahmniEventType.BAHMNI_PATIENT_UPDATED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventProcessorTest {

    @Test
    public void givenAdditionalPropertyKeyWithValue_whenStartProcessing_thenShouldAddInPayload() {

        RouteDescription routeDescription = mock(RouteDescription.class);
        Destination destination = new Destination(BAHMNI_PATIENT_UPDATED, new Topic("topicName", null), null);
        LinkedHashMap<String, String> staticProperties = new LinkedHashMap<>();
        staticProperties.put("facility", "Bahmni");
        AdditionalProperty additionalProperty = new AdditionalProperty("$", null, null, staticProperties, null, null);
        List<AdditionalProperty> additionalProperties = new ArrayList();
        additionalProperties.add(additionalProperty);
        when(routeDescription.getAdditionalProperties()).thenReturn(additionalProperties);
        when(routeDescription.getDestinationBasedOn("BAHMNI_PATIENT_UPDATED")).thenReturn(destination);


        EventProcessor eventProcessor = new EventProcessor(routeDescription);

        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);
        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(String.class)).thenReturn("{\"uuid\":\"patientUuid\"}");
        when(message.getHeader("eventType")).thenReturn("BAHMNI_PATIENT_UPDATED");

        assertDoesNotThrow(() -> eventProcessor.process(exchange));

        verify(message, times(1)).setBody("{\"uuid\":\"patientUuid\",\"facility\":\"Bahmni\"}");
    }

    @Test
    public void givenDestinationBasedOnEventType_whenStartProcessing_thenShouldChangeDestinationAsPerEventType() {

        RouteDescription routeDescription = mock(RouteDescription.class);
        Destination destination = new Destination(BAHMNI_PATIENT_UPDATED, new Topic("topicName", null), null);
        LinkedHashMap<String, String> staticProperties = new LinkedHashMap<>();
        staticProperties.put("facility", "Bahmni");
        AdditionalProperty additionalProperty = new AdditionalProperty("$", null, null, staticProperties, null, null);
        List<AdditionalProperty> additionalProperties = new ArrayList();
        additionalProperties.add(additionalProperty);
        when(routeDescription.getAdditionalProperties()).thenReturn(additionalProperties);
        when(routeDescription.getDestinationBasedOn("BAHMNI_PATIENT_UPDATED")).thenReturn(destination);

        EventProcessor eventProcessor = new EventProcessor(routeDescription);

        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);
        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(String.class)).thenReturn("{\"uuid\":\"patientUuid\"}");
        when(message.getHeader("eventType")).thenReturn("BAHMNI_PATIENT_UPDATED");

        assertDoesNotThrow(() -> eventProcessor.process(exchange));

        verify(exchange, times(1)).setProperty("destination", "topicName");
    }

    @Test
    public void givenAdditionalPropertiesAreEmpty_whenStartProcessing_thenShouldNotAddInPayload() {

        RouteDescription routeDescription = mock(RouteDescription.class);
        when(routeDescription.getAdditionalProperties()).thenReturn(new ArrayList());

        EventProcessor eventProcessor = new EventProcessor(routeDescription);

        Exchange exchange = mock(Exchange.class);

        assertDoesNotThrow(() -> eventProcessor.process(exchange));

        verify(exchange, times(0)).getIn(any());
    }

    @Test
    public void givenAdditionalPropertyKeyWithValueWithInvalidPayload_whenStartProcessing_thenShouldThrowRuntimeException() {

        RouteDescription routeDescription = mock(RouteDescription.class);
        LinkedHashMap<String, String> staticProperties = new LinkedHashMap<>();
        staticProperties.put("facility", "Bahmni");
        AdditionalProperty additionalProperty = new AdditionalProperty("$", null, null, staticProperties, null, null);
        List<AdditionalProperty> additionalProperties = new ArrayList();
        additionalProperties.add(additionalProperty);
        when(routeDescription.getAdditionalProperties()).thenReturn(additionalProperties);

        EventProcessor eventProcessor = new EventProcessor(routeDescription);

        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);
        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(String.class)).thenReturn("{uuid\":\"patientUuid\"}");

        assertThrows(RuntimeException.class, () -> eventProcessor.process(exchange));
    }
}