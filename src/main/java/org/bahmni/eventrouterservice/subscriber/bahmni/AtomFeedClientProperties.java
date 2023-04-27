package org.bahmni.eventrouterservice.subscriber.bahmni;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Data
@Configuration
@ConfigurationProperties(prefix = "bahmni")
public class AtomFeedClientProperties {
    private String authUri;
    private String user;
    private String password;
    private int connectionTimeoutInMilliseconds;
    private int replyTimeoutInMilliseconds;
    private int maxFailedEvents;

    public String baseURL() {
        try {
            URL openMRSAuthURL = new URL(authUri);
            return String.format("%s://%s", openMRSAuthURL.getProtocol(), openMRSAuthURL.getAuthority());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Is not a valid URL - " + authUri);
        }
    }
}