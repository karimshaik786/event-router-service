package org.bahmni.eventrouterservice.route;

import org.bahmni.eventrouterservice.configuration.BahmniAPIProperties;
import org.bahmni.webclients.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class BahmniAPIGateway {

    private final HttpClient bahmniHttpClient;
    private final BahmniAPIProperties bahmniAPIProperties;

    @Autowired
    public BahmniAPIGateway(HttpClient bahmniHttpClient, BahmniAPIProperties bahmniAPIProperties) {
        this.bahmniHttpClient = bahmniHttpClient;
        this.bahmniAPIProperties = bahmniAPIProperties;
    }

    public String getPatient(String patientUuid) {
        URI patientURI = URI.create(bahmniAPIProperties.getPatientUrl().formatted(patientUuid));
        return bahmniHttpClient.get(patientURI);
    }
}