package org.bahmni.eventrouterservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bahmni")
public class BahmniFeedProperties {
    private BahmniFeed feed;
    private BahmniOpenmrs openmrs;

    @Data
    public static class BahmniFeed {
        private int connectionTimeoutInMilliseconds;
        private int replyTimeoutInMilliseconds;
        private int maxFailedEvents;
        private int failedEventMaxRetry;
    }

    @Data
    public static class BahmniOpenmrs{
        private String host;
        private int port;
        private String authority;
        private String authUri;
        private String patientFeedUri;
        private String user;
        private String password;
        private int connectionTimeoutInMilliseconds;
        private int replyTimeoutInMilliseconds;
    }
}
