package org.bahmni.eventrouterservice.subscriber.bahmni;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bahmni")
public class AtomFeedClientProperties {
    private String authUrl;
    private String user;
    private String password;
    private int connectionTimeoutInMilliseconds;
    private int replyTimeoutInMilliseconds;
    private int maxFailedEvents;
    private String baseUrl;
}