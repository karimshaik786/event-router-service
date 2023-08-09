package org.bahmni.eventrouterservice.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.util.json.JsonObject;
import org.bahmni.eventrouterservice.configuration.BahmniAPIProperties;
import org.bahmni.webclients.HttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BahmniAPIGatewayTest {

    @Test
    public void givenPatientUUID_shouldFetchPatientInformation() throws IOException {
        HttpClient bahmniHttpClient = mock(HttpClient.class);
        BahmniAPIProperties bahmniAPIProperties = mock(BahmniAPIProperties.class);

        BahmniAPIGateway apiGateway = new BahmniAPIGateway(bahmniHttpClient, bahmniAPIProperties);

        String patientURL = "http://localhost:8080/openmrs/ws/rest/v1/patient/%s?v=full";
        String patientUuid = "25447fd7-1082-46ef-aa39-38914475f52e";
        URI patientURI = URI.create("http://localhost:8080/openmrs/ws/rest/v1/patient/25447fd7-1082-46ef-aa39-38914475f52e?v=full");
        File routeConfigurationFile = new FileSystemResource("src/test/resources/test-patient.json").getFile();
        JsonObject payload = new ObjectMapper().readValue(routeConfigurationFile, JsonObject.class);

        when(bahmniAPIProperties.getPatientUrl()).thenReturn(patientURL);
        when(bahmniHttpClient.get(patientURI)).thenReturn(payload.toJson());

        String patientJson = apiGateway.getPatient(patientUuid);

        Assertions.assertEquals(payload.toJson(), patientJson);
    }

    @Test
    public void givenPatientUUID_whileFetchingPatientInformation_throwWebException() throws IOException {
        HttpClient bahmniHttpClient = mock(HttpClient.class);
        BahmniAPIProperties bahmniAPIProperties = mock(BahmniAPIProperties.class);

        BahmniAPIGateway apiGateway = new BahmniAPIGateway(bahmniHttpClient, bahmniAPIProperties);

        String patientURL = "http://localhost:8080/openmrs/ws/rest/v1/patient/%s?v=full";
        String patientUuid = "25447fd7-1082-46ef-aa39-38914475f52e";
        URI patientURI = URI.create("http://localhost:8080/openmrs/ws/rest/v1/patient/25447fd7-1082-46ef-aa39-38914475f52e?v=full");

        when(bahmniAPIProperties.getPatientUrl()).thenReturn(patientURL);
        when(bahmniHttpClient.get(patientURI)).thenThrow(new RuntimeException("Web exception"));

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> apiGateway.getPatient(patientUuid));

        Assertions.assertEquals("Web exception", exception.getMessage());
    }

    @Test
    public void givenPatientUUID_whileFormingPatientURLToHit_throwRuntimeException() throws IOException {
        HttpClient bahmniHttpClient = mock(HttpClient.class);
        BahmniAPIProperties bahmniAPIProperties = mock(BahmniAPIProperties.class);

        BahmniAPIGateway apiGateway = new BahmniAPIGateway(bahmniHttpClient, bahmniAPIProperties);

        String patientUuid = "25447fd7-1082-46ef-aa39-38914475f52e";

        when(bahmniAPIProperties.getPatientUrl()).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> apiGateway.getPatient(patientUuid));
    }
}