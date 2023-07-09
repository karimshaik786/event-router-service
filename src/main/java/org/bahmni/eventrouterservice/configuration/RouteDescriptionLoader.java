package org.bahmni.eventrouterservice.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bahmni.eventrouterservice.exception.FailedToLoadConfiguration;
import org.bahmni.eventrouterservice.model.Queue;
import org.bahmni.eventrouterservice.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Slf4j
public class RouteDescriptionLoader {
    private List<RouteDescription> routeDescriptions = new ArrayList<>();

    @Autowired
    public RouteDescriptionLoader(@Value("${route.description.file.location}") String routeConfigurationFileLocation,
                                  ObjectMapper objectMapper) {
        loadConfiguration(routeConfigurationFileLocation, objectMapper);
    }

    private void loadConfiguration(String routeConfigurationFileLocation, ObjectMapper objectMapper) {

        try {
            File routeConfigurationFile = new FileSystemResource(routeConfigurationFileLocation).getFile();
            this.routeDescriptions = objectMapper.readValue(routeConfigurationFile, new TypeReference<>() {});
        } catch (IOException exception) {
            log.error("Failed to load configuration for file : " + routeConfigurationFileLocation);
            throw new FailedToLoadConfiguration(routeConfigurationFileLocation, exception);
        }
    }

    public List<RouteDescription> getRouteDescriptions() {
        return this.routeDescriptions;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class RouteDescription {
        private Source source;
        private Destination destination;
        private ErrorDestination errorDestination;
        private LinkedHashMap<String, String> additionalProperties;
        private LinkedHashMap<String, String> filterOnProperties;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class Source {
        private Topic topic;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class Destination {
        private Topic topic;
        private Queue queue;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class ErrorDestination extends Destination {
        private Integer maxRetryDelivery;
        private Long retryDeliveryDelayInMills;
        private String cronExpressionForRetryStart;
        private String cronExpressionForRetryStop;
    }
}
