package org.bahmni.eventrouterservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "bahmni.api")
@Getter
@Component
@Setter
public class BahmniAPIProperties {
    private String authUrl;
    private String patientUrl;
    private String user;
    private String password;
    private int connectionTimeoutInMilliseconds;
    private int replyTimeoutInMilliseconds;
}