package org.bahmni.eventrouterservice.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.util.json.JsonObject;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.RouteDescription;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import static org.mockito.Mockito.*;

public class DerivedPropertiesGeneratorTest {

    @Test
    public void givenListOfDerivedProperties_whileCreating_thenShouldGetCreated() throws IOException {

        RouteDescription routeDescription = mock(RouteDescription.class);
        LinkedHashMap<String, String> derivedProperties = new LinkedHashMap<>();
        derivedProperties.put("display", "$.identifiers[0].display");
        when(routeDescription.getDerivedProperties()).thenReturn(derivedProperties);

        DerivedPropertiesGenerator derivedPropertiesGenerator = new DerivedPropertiesGenerator(routeDescription);

        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);
        when(exchange.getIn()).thenReturn(message);
        File routeConfigurationFile = new FileSystemResource("src/test/resources/test-patient.json").getFile();
        JsonObject payload = new ObjectMapper().readValue(routeConfigurationFile, JsonObject.class);
        when(message.getBody(String.class)).thenReturn(payload.toJson());

        derivedPropertiesGenerator.process(exchange);

        verify(exchange, times(1)).setProperty("display", "Patient Identifier = BAH203262");
    }

    @Test
    public void givenListOfDerivedProperties_whileCreating_thenShouldNotGetCreated() throws IOException {

        RouteDescription routeDescription = mock(RouteDescription.class);
        LinkedHashMap<String, String> derivedProperties = new LinkedHashMap<>();
        derivedProperties.put("display", "$.identifiers[0].displayOne");
        when(routeDescription.getDerivedProperties()).thenReturn(derivedProperties);

        DerivedPropertiesGenerator derivedPropertiesGenerator = new DerivedPropertiesGenerator(routeDescription);

        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);
        when(exchange.getIn()).thenReturn(message);
        File routeConfigurationFile = new FileSystemResource("src/test/resources/test-patient.json").getFile();
        JsonObject payload = new ObjectMapper().readValue(routeConfigurationFile, JsonObject.class);
        when(message.getBody(String.class)).thenReturn(payload.toJson());

        derivedPropertiesGenerator.process(exchange);

        verify(exchange, times(1)).setProperty("display", null);
    }

    @Test
    public void givenEmptyListOfDerivedProperties_whileCreating_thenShouldNotGetCreated() throws IOException {

        RouteDescription routeDescription = mock(RouteDescription.class);
        when(routeDescription.getDerivedProperties()).thenReturn(Maps.newLinkedHashMap());

        DerivedPropertiesGenerator derivedPropertiesGenerator = new DerivedPropertiesGenerator(routeDescription);

        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);
        when(exchange.getIn()).thenReturn(message);
        File routeConfigurationFile = new FileSystemResource("src/test/resources/test-patient.json").getFile();
        JsonObject payload = new ObjectMapper().readValue(routeConfigurationFile, JsonObject.class);
        when(message.getBody(String.class)).thenReturn(payload.toJson());

        derivedPropertiesGenerator.process(exchange);

        verify(exchange, times(0)).setProperty(anyString(), any());
    }
}